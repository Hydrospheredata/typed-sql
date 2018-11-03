package typed.sql.prefixes

import shapeless.HList
import typed.sql._
import typed.sql.internal.FSHOps.CanJoin

class IJPrefix[A <: FSH, B, N, Rs <: HList, Ru <: HList](a: A, b: From[TRepr[B, N, Rs, Ru]]) {

  def on[C <: JoinCond](c: C)(
    implicit
    canJoin: CanJoin[A, From[TRepr[B, N, Rs, Ru]], C]
  ): IJ[TRepr[B, N, Rs, Ru], C, A] = IJ(b.repr, c, a)

}

class LJPrefix[A <: FSH, B, N, Rs <: HList, Ru <: HList](a: A, b: From[TRepr[B, N, Rs, Ru]]) {

  def on[C <: JoinCond](c: C)(
    implicit
    canJoin: CanJoin[A, From[TRepr[B, N, Rs, Ru]], C]
  ): LJ[TRepr[B, N, Rs, Ru], C, A] = LJ(b.repr, c, a)

}

class RJPrefix[A <: FSH, B, N, Rs <: HList, Ru <: HList](a: A, b: From[TRepr[B, N, Rs, Ru]]) {

  def on[C <: JoinCond](c: C)(
    implicit
    canJoin: CanJoin[A, From[TRepr[B, N, Rs, Ru]], C]
  ): RJ[TRepr[B, N, Rs, Ru], C, A] = RJ(b.repr, c, a)

}

class FJPrefix[A <: FSH, B, N, Rs <: HList, Ru <: HList](a: A, b: From[TRepr[B, N, Rs, Ru]]) {

  def on[C <: JoinCond](c: C)(
    implicit
    canJoin: CanJoin[A, From[TRepr[B, N, Rs, Ru]], C]
  ): FJ[TRepr[B, N, Rs, Ru], C, A] = FJ(b.repr, c, a)

}
