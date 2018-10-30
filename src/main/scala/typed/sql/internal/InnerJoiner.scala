package typed.sql.internal

import shapeless.HList
import typed.sql._

trait InnerJoiner[A <: SHead, B <: TableRepr[_, _, _], Q <: JoinCond] {
  type Out <: SHead
  def innerJoin(a: A, b: B, q: Q): Out
}

object InnerJoiner {

  type Aux[A <: SHead, B <: TableRepr[_, _, _], Q <: JoinCond, Out0] = InnerJoiner[A, B, Q] { type Out = Out0 }

  trait IsFieldOf[A <: TableRepr[_, _, _], C <: Column2[_, _, _]]
  object IsFieldOf {
    implicit def isFieldOf[A, N, R <: HList, K, N2, V](
      implicit
      ev: N =:= N2
    ): IsFieldOf[TableRepr[A, N, R], Column2[K, N2, V]] = null
  }

  trait IsCorrectCond[A <: TableRepr[_, _, _], B <: TableRepr[_, _, _], Q <: JoinCond]
  object IsCorrectCond {

    implicit def eqCond[A1, N1, R1 <: HList, A2, N2, R2 <: HList, K1, NN1, K2, NN2, V](
      implicit
      ev1: N1 =:= NN1,
      ev2: N2 =:= NN2
    ): IsCorrectCond[TableRepr[A1, N1, R1], TableRepr[A2, N2, R2], JoinCond.Eq[K1, V, NN1, K2, NN2]] = null
  }

  implicit def last[A <: TableRepr[_, _, _], B <: TableRepr[_, _, _], Q <: JoinCond](
    implicit
    isCorrectCond: IsCorrectCond[A, B, Q]
  ): Aux[SHNrm[A, JoinCond.NoCond, SE], B, Q, SHNrm[A, Q, SHNrm[B, JoinCond.NoCond, SE]]] = {
    new InnerJoiner[SHNrm[A, JoinCond.NoCond, SE], B, Q] {
      type Out = SHNrm[A, Q, SHNrm[B, JoinCond.NoCond, SE]]
      def innerJoin(a: SHNrm[A, JoinCond.NoCond, SE], b: B, q: Q): SHNrm[A, Q, SHNrm[B, JoinCond.NoCond, SE]] = {
        SHNrm(a.h, q, SHNrm(b, JoinCond.NoCond, SE))
      }
    }
  }

  implicit def shNRM[
    A <: TableRepr[_, _, _],
    D <: SHead,
    B <: TableRepr[_, _, _],
    Q  <: JoinCond,
    AQ <: JoinCond,
    NRO <: SHead,
    NOO <: SHead
  ](
    implicit
    isCorrectCond: IsCorrectCond[A, B, Q],
    append: ShapeOps.Append.Aux[SHNrm[A, AQ, D], B, Q, NRO, NOO]
  ): Aux[SHNrm[A, AQ, D], B, Q, NRO] = {
    new InnerJoiner[SHNrm[A, AQ, D], B, Q] {
      type Out = NRO
      def innerJoin(a: SHNrm[A, AQ, D], b: B, q: Q): NRO = {
        append.appNrm(a, q, b)
      }
    }
  }

}
