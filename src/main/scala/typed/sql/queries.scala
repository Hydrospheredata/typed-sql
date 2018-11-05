package typed.sql

import shapeless.{HList, HNil}

trait Delete[S <: FSH, In] {
  def astData: ast.Delete
  def in: In
}

object Delete {

  def create[A, N, Rs <: HList, Ru <: HList](from: From[TRepr[A, N, Rs, Ru]], tableName: String): Delete[From[TRepr[A, N, Rs, Ru]], HNil] = {
    new Delete[From[TRepr[A, N, Rs, Ru]], HNil] {
      val astData: ast.Delete = ast.Delete(tableName, None)
      val in: HNil = HNil
    }
  }
}

case class Insert[In](astData: ast.InsertInto, in: In)

trait Select[S <: FSH, Out, In] {
  type WhereFlag <: Select.HasWhere

  def astData: ast.Select[Out]
  def in: In
}

object Select {

  sealed trait HasWhere
  case object WhereDefined extends HasWhere
  case object WithoutWhere extends HasWhere

  def create[S <: FSH, Out](select: ast.Select[Out]): Select[S, Out, HNil] = {
    new Select[S, Out , HNil] {
      type WhereFlag = WithoutWhere.type
      val astData: ast.Select[Out] = select
      val in: HNil = HNil
    }
  }
}

trait Update[S <: FSH, In] {
  def astData: ast.Update
  def in: In
}
