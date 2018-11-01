package typed.sql

import shapeless.{HList, HNil}

trait Deletion[S <: FSH, In] {
  def astData: ast.Delete
  def in: In
}

object Deletion {

  def create[A, N, R <: HList](from: From[TRepr[A, N, R]], tableName: String): Deletion[From[TRepr[A, N, R]], HNil] = {
    new Deletion[From[TRepr[A, N, R]], HNil] {
      val astData: ast.Delete = ast.Delete(tableName, None)
      val in: HNil = HNil
    }
  }
}
