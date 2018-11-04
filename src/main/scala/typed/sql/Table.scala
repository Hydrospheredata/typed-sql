package typed.sql

import shapeless._
import shapeless.ops.record.Selector
import typed.sql.internal.RemoveFields
import typed.sql.prefixes._

case class Table[A, N, Rs <: HList, Ru <: HList](
  repr: TRepr[A, N, Rs, Ru],
  nameTyped: N,
  name: String
){ self =>

  final def column[V](k: Witness)(implicit sel: Selector.Aux[Rs, k.T, V]): Column[k.T, V, N] =
    new Column[k.T, V, N](k.value, nameTyped)

  final def col[V](k: Witness)(implicit sel: Selector.Aux[Rs, k.T, V]): Column[k.T, V, N] =
    new Column[k.T, V, N](k.value, nameTyped)

  def innerJoin[S2, N2, Rs2 <: HList, Ru2 <: HList](t2: Table[S2, N2, Rs2, Ru2]): IJPrefix[From[TRepr[A, N ,Rs, Ru]], S2, N2, Rs2, Ru2] =
    new IJPrefix(From(self.repr), From(t2.repr))

  def leftJoin[S2, N2, Rs2 <: HList, Ru2 <: HList](t2: Table[S2, N2, Rs2, Ru2]): LJPrefix[From[TRepr[A, N ,Rs, Ru]], S2, N2, Rs2, Ru2] =
    new LJPrefix(From(self.repr), From(t2.repr))

  def rightJoin[S2, N2, Rs2 <: HList, Ru2 <: HList](t2: Table[S2, N2, Rs2, Ru2]): RJPrefix[From[TRepr[A, N ,Rs, Ru]], S2, N2, Rs2, Ru2] =
    new RJPrefix(From(self.repr), From(t2.repr))

  def fullJoin[S2, N2, Rs2 <: HList, Ru2 <: HList](t2: Table[S2, N2, Rs2, Ru2]): FJPrefix[From[TRepr[A, N ,Rs, Ru]], S2, N2, Rs2, Ru2] =
    new FJPrefix(From(self.repr), From(t2.repr))
}

object Table {

  object of {
    def apply[A] = new TableBuild[A]

    class TableBuild[A] {

      def autoColumn[Rs <: HList, Ru <: HList](k: Witness)(
        implicit
        labGen: LabelledGeneric.Aux[A, Rs],
        rf: RemoveFields.Aux[Rs, k.T :: HNil, Ru]
      ): TableBuild2[A, Rs, Ru] = new TableBuild2

      def name[H <: HList](k: Witness)(
        implicit
        labGen: LabelledGeneric.Aux[A, H],
        ev: k.T <:< Symbol
      ): Table[A, k.T, H, H] = {
        val repr = new TRepr[A, k.T, H, H]()
        Table(repr, k.value, k.value.name)
      }

    }

    class TableBuild2[A, R <: HList, Ru <: HList] {

      def name(k: Witness)(
        implicit
        ev: k.T <:< Symbol
      ): Table[A, k.T, R, Ru] = {
        val repr = new TRepr[A, k.T, R, Ru]()
        Table(repr, k.value, k.value.name)
      }
    }
  }

}

