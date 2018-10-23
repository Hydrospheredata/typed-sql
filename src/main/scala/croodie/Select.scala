package croodie

import doobie.util.{Read, Write}
import doobie.util.fragment.Fragment
import doobie.util.param.Param
import doobie.util.query.Query0
import doobie.util.update.Update0
import shapeless.labelled.FieldType
import shapeless.ops.hlist.{SelectMany, Tupler}
import shapeless.ops.record.{Fields, Keys, Selector, Values}
import shapeless.{HList, HNil, LabelledGeneric, ProductArgs, Witness}
import shapeless._

sealed trait Expr[Out] {
  def fr: Fragment
}


case class Select[F <: HList, R](
  tableName: String,
  fields: List[String]
) { self =>

  def fr: Fragment = {
    val str = "SELECT " + fields.map(n => tableName + "." + n).mkString(", ") + " FROM " + tableName
    Fragment[HNil](str, HNil, None)(Param.ParamHNil.write)
  }

  def where[E <: ExprTree, O](expr: E)(implicit wi: WhereInfer.Aux[F, E, O]): Where[F, R, O] = {
    new Where(self, wi.in(expr), wi.expr)
  }

  def query(implicit read: Read[R]): Query0[R] = fr.query[R](read)

}

trait SelectionInfer[F <: HList, Q] {
  type Out
  def fields(): List[String]
}
trait LowPrioSelection {

  type Aux[F <: HList, Q, Out0] = SelectionInfer[F, Q] { type Out = Out0 }

  implicit def hnil[F <: HList]: Aux[F, HNil, HNil] = new SelectionInfer[F, HNil]{
    type Out = HNil
    def fields(): List[String] = List.empty
  }

  implicit def hCons[F <: HList, K, V, T <: HList, Z <: HList](
    implicit
    selector: Selector.Aux[F, K, V],
    wit: Witness.Aux[K],
    ev: K <:< Symbol,
    next: SelectionInfer.Aux[F, T, Z]
  ): Aux[F, K :: T, V :: Z] = {
    new SelectionInfer[F, K :: T] {
      type Out = V :: Z
      def fields(): List[String] = wit.value.name :: next.fields()
    }
  }
}

object SelectionInfer extends LowPrioSelection {

  type STAR = Witness.`'*`.T

  implicit def forStar[F <: HList, O <: HList](
    implicit
    names: FieldNames[F],
    values: Values.Aux[F, O]
  ): Aux[F, STAR :: HNil, O] = {
    new SelectionInfer[F, STAR :: HNil] {
      type Out = O
      def fields(): List[String] = names()
    }
  }
}


object Select {


  class Table[Orig, F <: HList](name: String) {

    object select extends ProductArgs {
      def applyProduct[Q, O](q: Q)(implicit inf: SelectionInfer.Aux[F, Q, O]): Select[F, O] =
        new Select(name, inf.fields())
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
