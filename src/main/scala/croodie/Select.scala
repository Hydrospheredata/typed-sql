package croodie

import doobie.util.Read
import doobie.util.fragment.Fragment
import doobie.util.param.Param
import doobie.util.query.Query0
import doobie.util.update.Update0
import shapeless.labelled.FieldType
import shapeless.ops.hlist.Selector
import shapeless.ops.record.Fields
import shapeless.{HList, HNil, LabelledGeneric}

sealed trait Expr[Out] {
  def fr: Fragment
}

class Select[Fields <: HList, Query <: HList](
  name: String,
  fields: List[String]
) {

  def fr: Fragment = {
    val str = "SELECT " + fields.map(n => name + "." + n).mkString(", ") + " FROM " + name
    Fragment[HNil](str, HNil, None)(Param.ParamHNil.write)
  }

  def query[R <: HList](implicit ft: FieldTypes.Aux[Query, R], r: Read[R]): Query0[R] = fr.query[R]

}


object Select {

  class Table[A <: HList](name: String) {

    def select[F, H <: HList, O <: HList](fields: F)(
      implicit
      hlister: HLister.Aux[F, H],
      fSelector: FieldSelector.Aux[A, H, O],
      names: FieldNames[O]
    ): Select[A, O] = {
      new Select[A, O](name, names())
    }

  }

  object tableOf {
    def apply[T] = new TableBuild[T]
    class TableBuild[T] {
      def name[H <: HList, F <: HList](name: String)(
        implicit labGen: LabelledGeneric.Aux[T, H],
        fields: CollectFields.Aux[H, F]
      ): Table[F] = new Table[F](name)
    }
  }

}
