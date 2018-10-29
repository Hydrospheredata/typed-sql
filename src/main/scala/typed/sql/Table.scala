package typed.sql

import croodie.internal.FieldNames
import shapeless._
import shapeless.ops.record.Selector
import typed.sql.prefixes.InnerJoinPrefix

sealed trait Shape
sealed trait SE extends Shape
case object SE extends SE

sealed trait SHead extends Shape
final case class SHOpt[H <: TableRepr[_, _, _], C <: JoinCond, T <: Shape](h: H, t: T) extends SHead
final case class SHNrm[H <: TableRepr[_, _, _], C <: JoinCond, T <: Shape](h: H, cnd: C, t: T) extends SHead

final case class TableRepr[S, N, R <: HList](
  lGen: LabelledGeneric.Aux[S, R]
)

case class Table3[S, N, R <: HList](
  repr: TableRepr[S, N, R],
  shape: SHNrm[TableRepr[S, N, R], JoinCond.NoCond, SE],
  nameTyped: N
) { self =>

  final def column[V](k: Witness)(implicit sel: Selector.Aux[R, k.T, V]): Column2[k.T, V, N] =
    new Column2[k.T, V, N](k.value, nameTyped)

  final def col[V](k: Witness)(implicit sel: Selector.Aux[R, k.T, V]): Column2[k.T, V, N] =
    new Column2[k.T, V, N](k.value, nameTyped)

  def innerJoin[S2, N2, R2 <: HList](t2: Table3[S2, N2, R2]): InnerJoinPrefix[SHNrm[TableRepr[S, N, R], JoinCond.NoCond, SE], TableRepr[S2, N2, R2]] =
    new InnerJoinPrefix(self.shape, t2.repr)
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
        val repr = TableRepr[A, k.T, H](labGen)
        //Table3[A, k.T, H](repr, SHNrm(repr, JoinCond.NoCond, SE), k.value)
        Table3(repr, SHNrm(repr, JoinCond.NoCond, SE), k.value)

//        new TableRepr[A, k.T, H] {
//          val nameT = k.value
//          val name: String = k.value.name
//          val columns: List[String] = fieldNames()
//        }
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

