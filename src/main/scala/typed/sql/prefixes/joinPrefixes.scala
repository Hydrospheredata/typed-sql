package typed.sql.prefixes

import shapeless.HList
import typed.sql._
import typed.sql.internal.FSHOps.CanJoin

final class IJPrefix[A, B](a: A, b: B) {
  def on[C <: JoinCond](c: C)(implicit canJoin: CanJoin[A, B, C]): IJ[B, C, A] = IJ(b, c, a)
}


//class LJPrefix[A <: FSH, B, Rs <: HList, Ru <: HList](a: A, b: From[TRepr[B, Rs, Ru]]) {
//
//  def on[C <: JoinCond](c: C)(
//    implicit
//    canJoin: CanJoin[A, From[TRepr[B, Rs, Ru]], C]
//  ): LJ[TRepr[B, Rs, Ru], C, A] = LJ(b.repr, c, a)
//
//}
//
//class RJPrefix[A <: FSH, B, Rs <: HList, Ru <: HList](a: A, b: From[TRepr[B, Rs, Ru]]) {
//
//  def on[C <: JoinCond](c: C)(
//    implicit
//    canJoin: CanJoin[A, From[TRepr[B, Rs, Ru]], C]
//  ): RJ[TRepr[B, Rs, Ru], C, A] = RJ(b.repr, c, a)
//
//}
//
//class FJPrefix[A <: FSH, B, Rs <: HList, Ru <: HList](a: A, b: From[TRepr[B, Rs, Ru]]) {
//
//  def on[C <: JoinCond](c: C)(
//    implicit
//    canJoin: CanJoin[A, From[TRepr[B, Rs, Ru]], C]
//  ): FJ[TRepr[B, Rs, Ru], C, A] = FJ(b.repr, c, a)
//
//}
