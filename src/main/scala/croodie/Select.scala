package croodie

import croodie.internal.FieldNames
import doobie.free.connection.ConnectionIO
import doobie.util.{Read, Write}
import doobie.util.fragment.Fragment
import doobie.util.param.Param
import doobie.util.query.Query0
import doobie.util.update.Update0
import shapeless.labelled.FieldType
import shapeless.ops.hlist.{SelectMany, Tupler}
import shapeless.ops.record._
import shapeless.{HList, HNil, LabelledGeneric, ProductArgs, Witness}
import shapeless._

case class Select[F <: HList, R](
  tableName: String,
  fields: List[String]
) { self =>

  def fr: Fragment = {
    val str = "SELECT " + fields.map(n => tableName + "." + n).mkString(", ") + " FROM " + tableName
    Fragment[HNil](str, HNil, None)(Param.ParamHNil.write)
  }

  def where[E <: ExprTree, O](expr: E)(implicit wi: WhereInfer.Aux[F, E, O]): Where[F, R, O] = {
    new Where(fr, tableName, wi.in(expr), wi.expr)
  }

  def query(implicit read: Read[R]): Query0[R] = fr.query[R](read)

}

trait SelectionInfer[Orig, F <: HList, Q] {
  type Out
  def fields(): List[String]
}
trait LowPrioSelection {

  type Aux[Orig, F <: HList, Q, Out0] = SelectionInfer[Orig, F, Q] { type Out = Out0 }

  implicit def hnil[Orig, F <: HList]: Aux[Orig, F, HNil, HNil] = new SelectionInfer[Orig, F, HNil]{
    type Out = HNil
    def fields(): List[String] = List.empty
  }

  implicit def hCons[Orig, F <: HList, K, V, T <: HList, Z <: HList](
    implicit
    selector: Selector.Aux[F, K, V],
    wit: Witness.Aux[K],
    ev: K <:< Symbol,
    next: SelectionInfer.Aux[Orig, F, T, Z]
  ): Aux[Orig, F, K :: T, V :: Z] = {
    new SelectionInfer[Orig, F, K :: T] {
      type Out = V :: Z
      def fields(): List[String] = wit.value.name :: next.fields()
    }
  }
}

object SelectionInfer extends LowPrioSelection {

  type STAR = Witness.`'*`.T

  implicit def forStar[Orig, F <: HList, O <: HList](
    implicit
    names: FieldNames[F]
  ): Aux[Orig, F, STAR :: HNil, Orig] = {
    new SelectionInfer[Orig, F, STAR :: HNil] {
      type Out = Orig
      def fields(): List[String] = names()
    }
  }
}

class UpsertOps[Orig, H <: HList, Id](
  tableName: String,
  idC: String,
  otherFields: List[String],
  from: Orig => (Id, H),
  to: (Id, Orig) => Orig,
  p1: Param[H],
  p2: Param[Id],
  r2: Read[Id]
) {

  def insertFr(in: H): Fragment = {
    val fStr = otherFields.mkString("(", ",", ")")
    val paramsStr = otherFields.map(_ => "?").mkString("(", ",", ")")
    val sql = s"INSERT INTO $tableName $fStr VALUES $paramsStr"
    Fragment[H](sql, in, None)(p1.write)
  }

  def insert(r: Orig): ConnectionIO[Orig] = {
    val (_, values) = from(r)
    val fr = insertFr(values)
    fr.update.withUniqueGeneratedKeys[Id](idC)(r2).map(id => to(id, r))
  }

  def updateFr(in: H, id: Id): Fragment = {
    val fields = otherFields.map(f => s"$f = ?").mkString(", ")
    val sql1 = s"UPDATE $tableName SET $fields"
    val fr1 = Fragment[H](sql1, in, None)(p1.write)
    val sql2 = s" WHERE $idC = ?"
    val fr2 = Fragment[Id](sql2, id, None)(p2.write)
    fr1 ++ fr2
  }

  def update(r: Orig) = {
    val (id, values) = from(r)
    val fr = updateFr(values, id)
    fr.update.run.map(_ => r)
  }
}


object Select {


  class Table[Orig, F <: HList](name: String) {

    object select extends ProductArgs {
      def applyProduct[Q, O](q: Q)(implicit inf: SelectionInfer.Aux[Orig, F, Q, O]): Select[F, O] =
        new Select(name, inf.fields())
    }

    object delete {
      def where[E <: ExprTree, O](expr: E)(implicit wi: WhereInfer.Aux[F, E, O]): Where[F, Int, O] = {
        val fr = Fragment[HNil](s"DELETE FROM $name", HNil, None)(Param.ParamHNil.write)
        new Where(fr, name, wi.in(expr), wi.expr)
      }
    }

    def upsertOps[All <: HList, H <: HList, K, V, Out1 <: HList, Z <: HList](id: Witness)(
      implicit
      labGen: LabelledGeneric.Aux[Orig, All],
      remover: Remover.Aux[All, id.T, (V, Out1)],
      values: Values.Aux[Out1, H],
      updater: Updater.Aux[All, FieldType[id.T, V], Z],
      ev: id.T <:< Symbol,
      names: FieldNames[F],
      param1: Param[H],
      param2: Param[V],
      read: Read[V]

    ): UpsertOps[Orig, H, V] = {
      val all = names()
      val idField = id.value.name
      val otherFields = all.filter(_ != idField)

      val from = (r: Orig) => {
        val rec = labGen.to(r)
        val (idKv, otherKv) = remover(rec)
        val id = idKv.asInstanceOf[V]
        val other = values(otherKv)
        (id , other)
      }
      val to = (idx: V, o: Orig) => {
        val rec = labGen.to(o)
        val upd = updater(rec, idx.asInstanceOf[FieldType[id.T, V]])
        labGen.from(upd.asInstanceOf[All])

      }
      new UpsertOps(name, idField, otherFields, from, to, param1, param2, read)
    }

  }

  object tableOf {
    def apply[Orig] = new TableBuild[Orig]
    class TableBuild[Orig] {
      def name[H <: HList, F <: HList](name: String)(
        implicit labGen: LabelledGeneric.Aux[Orig, H]
      ): Table[Orig, H] = new Table[Orig, H](name)
    }
  }

}
