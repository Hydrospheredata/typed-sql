package typed.sql

import shapeless._

trait Selection[S <: FSH, Out, In] {
  type WhereFlag <: Selection.HasWhere

  def astData: ast.Select[Out]
  def in: In
}

object Selection {

  sealed trait HasWhere
  case object WhereDefined extends HasWhere
  case object WithoutWhere extends HasWhere

  def create[S <: FSH, Out](select: ast.Select[Out]): Selection[S, Out, HNil] = {
    new Selection[S, Out , HNil] {
      type WhereFlag = WithoutWhere.type
      val astData: ast.Select[Out] = select
      val in: HNil = HNil
    }
  }
}


