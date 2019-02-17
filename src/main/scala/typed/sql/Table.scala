package typed.sql

import shapeless._
import shapeless.ops.record.Selector
import typed.sql.internal.{GetColumn, RemoveFields}
import typed.sql.prefixes._

final case class Table[A, Rs, Ru](name: String){ self =>
  
  type Self = Table[A, Rs, Ru]
  
  def column[V](k: Witness)(implicit ev: GetColumn.Aux[Rs, k.T, V]): Column[k.T, V, Self] = Column(k.value, name)
  def col[V](k: Witness)(implicit ev: GetColumn.Aux[Rs, k.T, V]): Column[k.T, V, Self] = Column(k.value, name)

//  def innerJoin[A2, Rs2, Ru2](t2: Table[A2, Rs2, Ru2]): IJPrefix[From[TR[A, Rs, Ru]], From[TR[A2, Rs2, Ru2]]] =
//    new IJPrefix(From(self), From(t2))

//  def leftJoin[S2, N2, Rs2 <: HList, Ru2 <: HList](t2: Table[S2, Rs2, Ru2]): LJPrefix[From[TRepr[A, Rs, Ru]], S2, N2, Rs2, Ru2] =
//    new LJPrefix(From(self.repr), From(t2.repr))
//
//  def rightJoin[S2, N2, Rs2 <: HList, Ru2 <: HList](t2: Table[S2, Rs2, Ru2]): RJPrefix[From[TRepr[A, Rs, Ru]], S2, N2, Rs2, Ru2] =
//    new RJPrefix(From(self.repr), From(t2.repr))
//
//  def fullJoin[S2, N2, Rs2 <: HList, Ru2 <: HList](t2: Table[S2, Rs2, Ru2]): FJPrefix[From[TRepr[A, Rs, Ru]], S2, N2, Rs2, Ru2] =
//    new FJPrefix(From(self.repr), From(t2.repr))
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

      def name[H <: HList](name: String)(implicit
        labGen: LabelledGeneric.Aux[A, H],
      ): Table[A, H, H] = new Table( name)

    }

    class TableBuild2[A, R <: HList, Ru <: HList] {
      def name(name: String): Table[A, R, Ru] = new Table(name)
    }
  }

}

