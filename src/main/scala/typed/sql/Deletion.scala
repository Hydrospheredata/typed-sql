package typed.sql

import shapeless.{HList, HNil}

trait Deletion[S <: FSH, In] {
  def astData: ast.Delete
  def in: In
}

object Deletion {

  def create[A, N, Rs <: HList, Ru <: HList](from: From[TRepr[A, N, Rs, Ru]], tableName: String): Deletion[From[TRepr[A, N, Rs, Ru]], HNil] = {
    new Deletion[From[TRepr[A, N, Rs, Ru]], HNil] {
      val astData: ast.Delete = ast.Delete(tableName, None)
      val in: HNil = HNil
    }
  }
}
