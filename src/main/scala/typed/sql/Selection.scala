package typed.sql

import shapeless._

trait Selection[Shape, R] {
  type Repr <: HList

  type In <: HList

  // is where was defined
  type WhereFlag <: Selection.HasWhere

  val labelledGeneric: LabelledGeneric.Aux[Shape, Repr]

  def tableName: String
  def fields: List[String]

  def sql: String
  def in: In
}

object Selection {

  sealed trait HasWhere
  case object WhereDefined extends HasWhere
  case object WithoutWhere extends HasWhere

  type Aux[Shape, R, Repr0 <: HList, In0, WhereFlag0] = Selection[Shape, R] {
    type Repr = Repr0
    type In = In0
    type WhereFlag = WhereFlag0
  }
}


