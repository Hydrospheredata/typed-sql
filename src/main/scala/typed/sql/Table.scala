package typed.sql

import poc.internal.FieldNames
import shapeless._
import shapeless.ops.record.Selector
import typed.sql.internal.RemoveFields
import typed.sql.prefixes._

case class Table[S, N, R <: HList](
  shape: From[TRepr[S, N, R]],
  nameTyped: N,
  name: String
) { self =>

  final def column[V](k: Witness)(implicit sel: Selector.Aux[R, k.T, V]): Column[k.T, V, N] =
    new Column[k.T, V, N](k.value, nameTyped)

  final def col[V](k: Witness)(implicit sel: Selector.Aux[R, k.T, V]): Column[k.T, V, N] =
    new Column[k.T, V, N](k.value, nameTyped)

  def innerJoin[S2, N2, R2 <: HList](t2: Table[S2, N2, R2]): IJPrefix[From[TRepr[S, N ,R]], S2, N2, R2] =
    new IJPrefix(self.shape, t2.shape)

  def leftJoin[S2, N2, R2 <: HList](t2: Table[S2, N2, R2]): LJPrefix[From[TRepr[S, N ,R]], S2, N2, R2] =
    new LJPrefix(self.shape, t2.shape)

  def rightJoin[S2, N2, R2 <: HList](t2: Table[S2, N2, R2]): RJPrefix[From[TRepr[S, N ,R]], S2, N2, R2] =
    new RJPrefix(self.shape, t2.shape)

  def fullJoin[S2, N2, R2 <: HList](t2: Table[S2, N2, R2]): FJPrefix[From[TRepr[S, N ,R]], S2, N2, R2] =
    new FJPrefix(self.shape, t2.shape)
}


object Table{

  object of {
    def apply[A] = new TableBuild[A]

    class TableBuild[A] {
      def name[H <: HList](k: Witness)(
        implicit
        labGen: LabelledGeneric.Aux[A, H],
        ev: k.T <:< Symbol,
        fieldNames: FieldNames[H]
      ): Table[A, k.T, H] = {
        val repr = TRepr[A, k.T, H](labGen)
        Table(From(repr), k.value, k.value.name)
      }
    }
  }
}

case class TableUpd[A, N, Rs <: HList, Ru <: HList](
  repr: TRepr2[A, N, Rs, Ru],
  nameTyped: N,
  name: String
){

  final def column[V](k: Witness)(implicit sel: Selector.Aux[Rs, k.T, V]): Column[k.T, V, N] =
    new Column[k.T, V, N](k.value, nameTyped)

  final def col[V](k: Witness)(implicit sel: Selector.Aux[Rs, k.T, V]): Column[k.T, V, N] =
    new Column[k.T, V, N](k.value, nameTyped)
}

object TableUpd {

  object of {
    def apply[A] = new TableBuild[A]

    class TableBuild[A] {

      def primaryKey[Rs <: HList, Ru <: HList](k: Witness)(
        implicit
        labGen: LabelledGeneric.Aux[A, Rs],
        rf: RemoveFields.Aux[Rs, k.T :: HNil, Ru]
      ): TableBuild2[A, Rs, Ru] = new TableBuild2

      def name[H <: HList](k: Witness)(
        implicit
        labGen: LabelledGeneric.Aux[A, H],
        ev: k.T <:< Symbol
      ): TableUpd[A, k.T, H, HNil] = {
        val repr = new TRepr2[A, k.T, H, HNil]()
        TableUpd(repr, k.value, k.value.name)
      }

    }

    class TableBuild2[A, R <: HList, Ru <: HList] {

      def name(k: Witness)(
        implicit
        ev: k.T <:< Symbol
      ): TableUpd[A, k.T, R, Ru] = {
        val repr = new TRepr2[A, k.T, R, Ru]()
        TableUpd(repr, k.value, k.value.name)
      }
    }
  }

}

