package croodie

import doobie.util.Read
import doobie.util.fragment.Fragment
import doobie.util.param.Param
import doobie.util.query.Query0
import doobie.util.update.Update0
import shapeless.labelled.FieldType
import shapeless.ops.hlist.{Selector, Tupler}
import shapeless.ops.record.Fields
import shapeless.{HList, HNil, LabelledGeneric, Witness}

sealed trait Expr[Out] {
  def fr: Fragment
}


class Select[F <: HList, R](
  name: String,
  fields: List[String],
  read: Read[R]
) {

  def fr: Fragment = {
    val str = "SELECT " + fields.map(n => name + "." + n).mkString(", ") + " FROM " + name
    Fragment[HNil](str, HNil, None)(Param.ParamHNil.write)
  }

  def query: Query0[R] = fr.query[R](read)

}

/**
  * Orig - table case class
  * F - all table fields
  * Q - selection
  */
trait SelectBuilder[Orig, F <: HList, Q] {
  type Out
  def mk(tableName: String): Select[F, Out]
}

trait LowPrioSelectBuilder {

  type Aux[Orig, F <: HList, Q, Out0] = SelectBuilder[Orig, F, Q] { type Out = Out0 }

  implicit def select[Orig, F <: HList, Q, H <: HList, O <: HList, OT <: HList, Tuple](
    implicit
    hlister: HLister.Aux[Q, H],
    fSelector: FieldSelector.Aux[F, H, O],
    names: FieldNames[O],
    ft: FieldTypes.Aux[O, OT],
    tupler: Tupler.Aux[OT, Tuple],
    read: Read[OT]
  ): Aux[Orig, F, Q, Tuple] = {
    new SelectBuilder[Orig, F, Q] {
      type Out = Tuple
      def mk(tableName: String): Select[F, Tuple] =
        new Select[F, Tuple](tableName, names(), read.map(hlist => tupler(hlist)))
    }
  }
}

object SelectBuilder extends LowPrioSelectBuilder {
  implicit def forStar[Orig, F <: HList, Q](
    implicit
    ev: Q =:= Witness.`'*`.T,
    names: FieldNames[F],
    r: Read[Orig]
  ): Aux[Orig, F, Q, Orig] = {
    new SelectBuilder[Orig, F, Q] {
      type Out = Orig
      def mk(tableName: String): Select[F, Orig] = new Select[F, Orig](tableName, names(), r)
    }
  }
}

object Select {


  class Table[Orig, F <: HList](name: String) {

    def select[Q, O](query: Q)(implicit b: SelectBuilder.Aux[Orig, F, Q, O]): Select[F, O] = b.mk(name)
  }

  object tableOf {
    def apply[Orig] = new TableBuild[Orig]
    class TableBuild[Orig] {
      def name[H <: HList, F <: HList](name: String)(
        implicit labGen: LabelledGeneric.Aux[Orig, H],
        fields: CollectFields.Aux[H, F]
      ): Table[Orig, F] = new Table[Orig, F](name)
    }
  }

}
