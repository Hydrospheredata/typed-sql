package typed.sql

import croodie.internal.FieldNames
import shapeless._
import shapeless.ops.record.Selector


/**
  *  Table result representation to support joins
  *
  *  table(a: Int, b: String) === Normal('a -> Int, 'b -> String, Nil)
  *
  *  table(a: Int, b: String)
  *    innerjoin
  *    table(c: Boolean)      === Normal('a -> Int, 'b -> String, Normal('c -> Boolean, Nil))
  *    
  *  table(a: Int, b: String)
  *    leftJoin
  *    table(c: Boolean)      === Normal('a -> Int, 'b -> String, Optional('c -> Boolean, Nil))
  *
  */
sealed trait SelectType
object SelectType {

  case object Nil extends SelectType

  final class Normal[Shape, Repr](
    headGen: LabelledGeneric.Aux[Shape, Repr],
    tail: SelectType
  ) extends SelectType

  final class Optional[Shape, Repr](
    headGen: LabelledGeneric.Aux[Shape, Repr],
    tail: SelectType
  ) extends SelectType
}

/**
  * Repr ->
  */
trait Table[Shape] {

  //type Repr <: HList
//  type SelectRepr <: SelectType

  //val labelledGeneric: LabelledGeneric.Aux[Shape, Repr]

  def name: String
  def columns: List[String]

  def col[V](k: Witness)(
    implicit
    sel: Selector.Aux[Repr, k.T, V]
  ): Column[k.T, V] = Column[k.T, V](k.value)


  def leftJoin[B, R2]()

}

object Table {

  object of {
    def apply[A] = new TableBuild[A]

    class TableBuild[A] {
      def name[H <: HList](tableName: String)(
        implicit
        labGen: LabelledGeneric.Aux[A, H],
        fieldNames: FieldNames[H]
      ): Table = {
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

