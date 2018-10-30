package typed.sql.internal

import typed.sql._

object ShapeOps {

  trait Append[In <: Shape, T <: TableRepr[_, _, _], Q <: JoinCond] {
    type OutNrm
    type OutOpt
    def appNrm(in: In, q: Q, t: T): OutNrm
    def appOpt(in: In, q: Q, t: T): OutOpt
  }

  object Append {

    type Aux[In <: Shape, T <: TableRepr[_, _, _], Q <: JoinCond, OutNrm0, OutOpt0] = Append[In, T, Q]{
      type OutNrm = OutNrm0
      type OutOpt = OutOpt0
    }

    implicit def shNRMSE[
      R <: TableRepr[_, _, _],
      C <: JoinCond,
      Q <: JoinCond,
      T <: TableRepr[_, _, _],
      ON, OO
    ]: Aux[SHNrm[R, JoinCond.NoCond, SE], T, Q, SHNrm[R, Q, SHNrm[T, JoinCond.NoCond, SE]], SHNrm[R, Q, SHOpt[T, JoinCond.NoCond, SE]]] = {
      new Append[SHNrm[R, JoinCond.NoCond, SE], T, Q] {
        type OutNrm = SHNrm[R, Q, SHNrm[T, JoinCond.NoCond, SE]]
        type OutOpt = SHNrm[R, Q, SHOpt[T, JoinCond.NoCond, SE]]

        def appNrm(in: SHNrm[R, JoinCond.NoCond, SE], q: Q, t: T): OutNrm = SHNrm(in.h, q, SHNrm(t, JoinCond.NoCond, SE))
        def appOpt(in: SHNrm[R, JoinCond.NoCond, SE], q: Q, t: T): OutOpt = SHNrm(in.h, q, SHOpt(t, JoinCond.NoCond, SE))
      }
    }

    implicit def shOptSE[
      R <: TableRepr[_, _, _],
      C <: JoinCond,
      Q <: JoinCond,
      N <: SHead,
      T <: TableRepr[_, _, _],
      ON, OO
    ]: Aux[SHOpt[R, JoinCond.NoCond, SE], T, Q, SHOpt[R, Q, SHNrm[T, JoinCond.NoCond, SE]], SHOpt[R, Q, SHOpt[T, JoinCond.NoCond, SE]]] = {
      new Append[SHOpt[R, JoinCond.NoCond, SE], T, Q] {
        type OutNrm = SHOpt[R, Q, SHNrm[T, JoinCond.NoCond, SE]]
        type OutOpt = SHOpt[R, Q, SHOpt[T, JoinCond.NoCond, SE]]

        def appNrm(in: SHOpt[R, JoinCond.NoCond, SE], q: Q, t: T): OutNrm = SHOpt(in.h, q, SHNrm(t, JoinCond.NoCond, SE))
        def appOpt(in: SHOpt[R, JoinCond.NoCond, SE], q: Q, t: T): OutOpt = SHOpt(in.h, q, SHOpt(t, JoinCond.NoCond, SE))
      }
    }


    implicit def shNRM[
      R <: TableRepr[_, _, _],
      C <: JoinCond,
      Q <: JoinCond,
      N <: SHead,
      T <: TableRepr[_, _, _],
      ON, OO
    ](
      implicit nextApp : Append.Aux[N, T, Q, ON, OO]
    ): Aux[SHNrm[R, C, N], T, Q, ON, OO] = {

      new Append[SHNrm[R, C, N], T, Q] {
        type OutNrm = ON
        type OutOpt = OO
        def appNrm(in: SHNrm[R, C, N], q: Q, t: T): OutNrm = nextApp.appNrm(in.t, q, t)
        def appOpt(in: SHNrm[R, C, N], q: Q, t: T): OutOpt = nextApp.appOpt(in.t, q, t)
      }
    }

    implicit def shOpt[
      R <: TableRepr[_, _, _],
      C <: JoinCond,
      Q <: JoinCond,
      N <: SHead,
      T <: TableRepr[_, _, _],
      ON, OO
    ](
      implicit nextApp : Append.Aux[N, T, Q, ON, OO]
    ): Aux[SHOpt[R, C, N], T, Q, ON, OO] = {

      new Append[SHOpt[R, C, N], T, Q] {
        type OutNrm = ON
        type OutOpt = OO
        def appNrm(in: SHOpt[R, C, N], q: Q, t: T): OutNrm = nextApp.appNrm(in.t, q, t)
        def appOpt(in: SHOpt[R, C, N], q: Q, t: T): OutOpt = nextApp.appOpt(in.t, q, t)
      }
    }
  }
}
