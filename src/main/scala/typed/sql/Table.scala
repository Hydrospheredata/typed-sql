package typed.sql

import poc.internal.FieldNames
import shapeless._
import shapeless.ops.record.Selector
import typed.sql.prefixes._


case class Table3[S, N, R <: HList](
  repr: TRepr[S, N, R],
  shape: From[TRepr[S, N, R]],
  nameTyped: N
) { self =>

  final def column[V](k: Witness)(implicit sel: Selector.Aux[R, k.T, V]): Column2[k.T, V, N] =
    new Column2[k.T, V, N](k.value, nameTyped)

  final def col[V](k: Witness)(implicit sel: Selector.Aux[R, k.T, V]): Column2[k.T, V, N] =
    new Column2[k.T, V, N](k.value, nameTyped)

  def innerJoin[S2, N2, R2 <: HList](t2: Table3[S2, N2, R2]): IJPrefix[From[TRepr[S, N ,R]], S2, N2, R2] =
    new IJPrefix(self.shape, t2.shape)

  def leftJoin[S2, N2, R2 <: HList](t2: Table3[S2, N2, R2]): LJPrefix[From[TRepr[S, N ,R]], S2, N2, R2] =
    new LJPrefix(self.shape, t2.shape)

  def rightJoin[S2, N2, R2 <: HList](t2: Table3[S2, N2, R2]): RJPrefix[From[TRepr[S, N ,R]], S2, N2, R2] =
    new RJPrefix(self.shape, t2.shape)

  def fullJoin[S2, N2, R2 <: HList](t2: Table3[S2, N2, R2]): FJPrefix[From[TRepr[S, N ,R]], S2, N2, R2] =
    new FJPrefix(self.shape, t2.shape)
}


object Table3{

  object of {
    def apply[A] = new TableBuild[A]

    class TableBuild[A] {
      def name[H <: HList](k: Witness)(
        implicit
        labGen: LabelledGeneric.Aux[A, H],
        ev: k.T <:< Symbol,
        fieldNames: FieldNames[H]
      ): Table3[A, k.T, H] = {
        val repr = TRepr[A, k.T, H](labGen)
        Table3(repr, From(repr), k.value)
      }
    }
  }
}

/**
  * Repr ->
  */
trait Table[Shape] {

  type Repr <: HList
  //  type SelectRepr <: SelectType

  val labelledGeneric: LabelledGeneric.Aux[Shape, Repr]

  def name: String
  def columns: List[String]

  def col[V](k: Witness)(
    implicit
    sel: Selector.Aux[Repr, k.T, V]
  ): Column[k.T, V] = Column[k.T, V](k.value)


}

object Table {

  object of {
    def apply[A] = new TableBuild[A]

    class TableBuild[A] {
      def name[H <: HList](tableName: String)(
        implicit
        labGen: LabelledGeneric.Aux[A, H],
        fieldNames: FieldNames[H]
      ): Table[A] { type Repr = H } = {
        new Table[A] {
          type Repr = H

          val labelledGeneric: LabelledGeneric.Aux[A, H] = labGen
          val name: String = tableName
          val columns: List[String] = fieldNames()
        }
      }
    }
  }
}

