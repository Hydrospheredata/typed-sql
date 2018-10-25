package typed.sql

import croodie.internal.FieldNames
import shapeless._
import shapeless.ops.record.Selector

trait Table[Shape] {
  type Repr <: HList
  val labelledGeneric: LabelledGeneric.Aux[Shape, Repr]

  def name: String
  def columns: List[String]

  def col[V](k: Witness)(
    implicit
    sel: Selector.Aux[Repr, k.T, V]
  ): Column[k.T, V] = Column[k.T, V](k.value)


}

object Table {

  type Aux[Shape, Repr0] = Table[Shape] { type Repr = Repr0 }

  object of {

    def apply[Orig] = new TableBuild[Orig]

    class TableBuild[A] {
      def name[H <: HList](tableName: String)(
        implicit
        labGen: LabelledGeneric.Aux[A, H],
        fieldNames: FieldNames[H]
      ): Table.Aux[A, H] = {
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

