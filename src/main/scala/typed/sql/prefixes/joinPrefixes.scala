package typed.sql.prefixes

import shapeless.HList
import typed.sql._
import typed.sql.internal.FSHOps.CanJoin

class IJPrefix[A <: FSH, B, N, R <: HList](a: A, b: From[TRepr[B, N, R]]) {

  def on[C <: JoinCond](c: C)(
    implicit
    canJoin: CanJoin[A, From[TRepr[B, N, R]], C]
  ): IJ[TRepr[B, N, R], C, A] = IJ(b.repr, c, a)

}

class LJPrefix[A <: FSH, B, N, R <: HList](a: A, b: From[TRepr[B, N, R]]) {

  def on[C <: JoinCond](c: C)(
    implicit
    canJoin: CanJoin[A, From[TRepr[B, N, R]], C]
  ): LJ[TRepr[B, N, R], C, A] = LJ(b.repr, c, a)

}

class RJPrefix[A <: FSH, B, N, R <: HList](a: A, b: From[TRepr[B, N, R]]) {

  def on[C <: JoinCond](c: C)(
    implicit
    canJoin: CanJoin[A, From[TRepr[B, N, R]], C]
  ): RJ[TRepr[B, N, R], C, A] = RJ(b.repr, c, a)

}

class FJPrefix[A <: FSH, B, N, R <: HList](a: A, b: From[TRepr[B, N, R]]) {

  def on[C <: JoinCond](c: C)(
    implicit
    canJoin: CanJoin[A, From[TRepr[B, N, R]], C]
  ): FJ[TRepr[B, N, R], C, A] = FJ(b.repr, c, a)

}
