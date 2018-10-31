package typed.sql.internal

import shapeless._
import shapeless.ops.record.Selector
import typed.sql._

import scala.annotation.implicitNotFound

object FSHOps {

  trait IsFieldOf[A <: FSH, Name]

  object IsFieldOf {

    implicit def forFrom[N, N2, a, r <: HList](
      implicit
      ev: N =:= N2
    ): IsFieldOf[From[TRepr[a, N, r]], N2] = null

    implicit def forIJ[N, N2, a, r <: HList, c <: JoinCond, t <: FSH](
      implicit ev: N =:= N2
    ): IsFieldOf[IJ[TRepr[a, N, r], c, t], N2] = null

    implicit def forIJRecurse[T <: FSH, a, n2, r <: HList, N, R, c <: JoinCond](
      implicit next: IsFieldOf[T, N]
    ): IsFieldOf[IJ[TRepr[a, n2, r], c, T], N] = null

    implicit def forLJ[N, N2, a, r <: HList, c <: JoinCond, t <: FSH](
      implicit ev: N =:= N2
    ): IsFieldOf[LJ[TRepr[a, N, r], c, t], N2] = null

    implicit def forLJRecurse[T <: FSH, a, n2, r <: HList, N, R, c <: JoinCond](
      implicit next: IsFieldOf[T, N]
    ): IsFieldOf[LJ[TRepr[a, n2, r], c, T], N] = null

    implicit def forRJ[N, N2, a, r <: HList, c <: JoinCond, t <: FSH](
      implicit ev: N =:= N2
    ): IsFieldOf[RJ[TRepr[a, N, r], c, t], N2] = null

    implicit def forRJRecurse[T <: FSH, a, n2, r <: HList, N, R, c <: JoinCond](
      implicit next: IsFieldOf[T, N]
    ): IsFieldOf[RJ[TRepr[a, n2, r], c, T], N] = null

    implicit def forFJ[N, N2, a, r <: HList, c <: JoinCond, t <: FSH](
      implicit ev: N =:= N2
    ): IsFieldOf[FJ[TRepr[a, N, r], c, t], N2] = null

    implicit def forFJRecurse[T <: FSH, a, n2, r <: HList, N, R, c <: JoinCond](
      implicit next: IsFieldOf[T, N]
    ): IsFieldOf[FJ[TRepr[a, n2, r], c, T], N] = null

    //def apply[A <: FSH, N](implicit v: IsFieldOf[A, N]): IsFieldOf[A, N] = v
    def apply[A <: FSH](fsh: A, k: Witness)(implicit v: IsFieldOf[A, k.T]): IsFieldOf[A, k.T] = v
  }

  @implicitNotFound("\nCan't prove that join operation is possible \n${A} \n\twith \n\t${B} \n\tusing\n\t${C}. \nCheck that you use columns that belongs to joining tables")
  trait CanJoin[A <: FSH, B <: From[_], C <: JoinCond]
  object CanJoin {

    implicit def canJoinEq1[A <: FSH, R <: TR, B <: From[_], T1, T2, k1, k2, v](
      implicit
      f1: IsFieldOf[A, T1],
      f2: IsFieldOf[B, T2]
    ): CanJoin[A, B, JoinCond.Eq[k1, v, T1, k2, T2]] = null

    implicit def canJoinEq2[A <: FSH, R <: TR, B <: From[_], T1, T2, k1, k2, v](
      implicit
      f1: IsFieldOf[A, T2],
      f2: IsFieldOf[B, T1]
    ): CanJoin[A, B, JoinCond.Eq[k1, v, T1, k2, T2]] = null
  }


  trait AllColumns[A <: FSH] {
    def columns: List[ast.Col]
  }

  object AllColumns {

    trait FromRepr[T <: TR] {
      def columns: List[ast.Col]
    }
    object FromRepr {

      implicit def fromRepr[a, N, R <: HList](
        implicit
        fieldNames: FieldNames[R],
        wt: Witness.Aux[N],
        ev: N <:< Symbol
      ): FromRepr[TRepr[a, N, R]] = {
        new FromRepr[TRepr[a, N, R]] {
          override def columns: List[ast.Col] = {
            val table = wt.value.name
            fieldNames().map(n => ast.Col(table, n))
          }
        }

      }
    }

    private def create[A <: FSH](f: => List[ast.Col]): AllColumns[A] = new AllColumns[A] {
      override def columns: List[ast.Col] = f
    }

    implicit def forFrom[T <: TR](
      implicit fromRepr: FromRepr[T]
    ): AllColumns[From[T]] = create(fromRepr.columns)

    implicit def forIJ[T <: TR, c <: JoinCond, tail <: FSH](
      implicit
      fromRepr1: FromRepr[T],
      rest: AllColumns[tail]
    ): AllColumns[IJ[T, c, tail]] = create(fromRepr1.columns ++ rest.columns)

    implicit def forLJ[T <: TR, c <: JoinCond, tail <: FSH](
      implicit
      fromRepr1: FromRepr[T],
      rest: AllColumns[tail]
    ): AllColumns[LJ[T, c, tail]] = create(fromRepr1.columns ++ rest.columns)

    implicit def forRJ[T <: TR, c <: JoinCond, tail <: FSH](
      implicit
      fromRepr1: FromRepr[T],
      rest: AllColumns[tail]
    ): AllColumns[RJ[T, c, tail]] = create(fromRepr1.columns ++ rest.columns)

    implicit def forFJ[T <: TR, c <: JoinCond, tail <: FSH](
      implicit
      fromRepr1: FromRepr[T],
      rest: AllColumns[tail]
    ): AllColumns[FJ[T, c, tail]] = create(fromRepr1.columns ++ rest.columns)
  }


  trait JoinCondInfer[C <: JoinCond] {
    def mkAst(): ast.JoinCond
  }

  // TODO AND ? OR?
  object JoinCondInfer {

    implicit def forEq[K1, v, T1, K2, T2](
      implicit
      wt1: Witness.Aux[K1],
      ev1: K1 <:< Symbol,
      wt2: Witness.Aux[T1],
      ev2: T1 <:< Symbol,
      wt3: Witness.Aux[K2],
      ev3: K2 <:< Symbol,
      wt4: Witness.Aux[T2],
      ev4: T2 <:< Symbol,
    ): JoinCondInfer[JoinCond.Eq[K1, v, T1, K2, T2]] = {
      new JoinCondInfer[JoinCond.Eq[K1, v, T1, K2, T2]] {
        override def mkAst(): ast.JoinCond =
          ast.JoinCondEq(ast.Col(wt2.value.name, wt1.value.name), ast.Col(wt4.value.name, wt3.value.name))
      }
    }
  }

  trait SelectField[A <: FSH, K] {
    type Out
  }

  object SelectField {
    type Aux[A <: FSH, K, Out0] = SelectField[A, K] { type Out = Out0 }

    implicit def from[a, n, R <: HList, K, V](
      implicit
      sel: Selector.Aux[R, K, V]
    ): Aux[From[TRepr[a, n, R]], K, V] = null

    implicit def ij1[a, n, R <: HList, K, V, c <: JoinCond, tail <: FSH](
      implicit
      sel: Selector.Aux[R, K, V]
    ): Aux[IJ[TRepr[a, n, R], c, tail], K, V] = null

    implicit def ij2[a, n, R <: HList, K, V, c <: JoinCond, TAIL <: FSH, O](
      implicit
      next: SelectField.Aux[TAIL, K, O]
    ): Aux[IJ[TRepr[a, n, R], c, TAIL], K, O] = null

    implicit def lj1[a, n, R <: HList, K, V, c <: JoinCond, tail <: FSH, O](
      implicit
      sel: Selector.Aux[R, K, V],
      fl: FlattenOption.Aux[V, O]
    ): Aux[LJ[TRepr[a, n, R], c, tail], K, O] = null

    implicit def lj2[a, n, R <: HList, K, V, c <: JoinCond, TAIL <: FSH, O](
      implicit
      next: SelectField.Aux[TAIL, K, O]
    ): Aux[LJ[TRepr[a, n, R], c, TAIL], K, O] = null

    implicit def rj1[a, n, R <: HList, K, V, c <: JoinCond, tail <: FSH](
      implicit
      sel: Selector.Aux[R, K, V]
    ): Aux[RJ[TRepr[a, n, R], c, tail], K, V] = null

    implicit def rj2[a, n, R <: HList, K, V, c <: JoinCond, TAIL <: FSH, O, O2](
      implicit
      next: SelectField.Aux[TAIL, K, O],
      fl: FlattenOption.Aux[O, O2]
    ): Aux[RJ[TRepr[a, n, R], c, TAIL], K, O2] = null

    implicit def fj1[a, n, R <: HList, K, V, c <: JoinCond, tail <: FSH, O](
      implicit
      sel: Selector.Aux[R, K, V],
      fl: FlattenOption.Aux[V, O]
    ): Aux[FJ[TRepr[a, n, R], c, tail], K, O] = null

    implicit def fj2[a, n, R <: HList, K, V, c <: JoinCond, TAIL <: FSH, O](
      implicit
      next: SelectField.Aux[TAIL, K, O],
      fl: FlattenOption.Aux[V, O]
    ): Aux[FJ[TRepr[a, n, R], c, TAIL], K, O] = null
  }


  trait LowLevelSelectInfer[A <: FSH, Q] {
    type Out <: HList
    def cols: List[ast.Col]
  }

  object LowLevelSelectInfer {
    type Aux[A <: FSH, Q, Out0] = LowLevelSelectInfer[A, Q] { type Out = Out0 }

    implicit def hnil[A <: FSH]: Aux[A, HNil, HNil] = {
      new LowLevelSelectInfer[A, HNil] {
        type Out = HNil
        def cols: List[ast.Col] = List.empty
      }
    }

    implicit def hCons[A <: FSH, TName, K, v, T <: HList, O <: HList, X](
      implicit
      sel: SelectField.Aux[A, K, X],
      next: LowLevelSelectInfer.Aux[A, T, O],
      w1: Witness.Aux[TName],
      ev1: TName <:< Symbol,
      w2: Witness.Aux[K],
      ev2: K <:< Symbol
    ): Aux[A, Column[K, v, TName] :: T, X :: O] = {
      new LowLevelSelectInfer[A, Column[K, v, TName] :: T] {
        type Out = X :: O
        def cols: List[ast.Col] = ast.Col(w1.value.name, w2.value.name) :: next.cols
      }
    }

  }

  trait FromInfer[A <: FSH] {
    def mkAst(shape: A): ast.From
  }
  object FromInfer {

    private def create[A <: FSH](f: A => ast.From): FromInfer[A] = new FromInfer[A] {
      override def mkAst(shape: A): ast.From = f(shape)
    }

    implicit def from[a, N, r <: HList](
      implicit
      wt: Witness.Aux[N],
      ev: N <:< Symbol
    ): FromInfer[From[TRepr[a, N, r]]] = create(_ => ast.From(wt.value.name, List.empty))

    implicit def ij[a, N, r <: HList, C <: JoinCond, TAIL <: FSH](
      implicit
      wt: Witness.Aux[N],
      ev: N <:< Symbol,
      cndInf: JoinCondInfer[C],
      tInf: FromInfer[TAIL]
    ): FromInfer[IJ[TRepr[a, N, r], C, TAIL]] = {

      create(a => {
        val x = tInf.mkAst(a.tail)
        val j = ast.InnerJoin(wt.value.name, cndInf.mkAst())
        ast.From(x.table, x.joins :+ j)
      })
    }

    implicit def lj[a, N, r <: HList, C <: JoinCond, TAIL <: FSH](
      implicit
      wt: Witness.Aux[N],
      ev: N <:< Symbol,
      cndInf: JoinCondInfer[C],
      tInf: FromInfer[TAIL]
    ): FromInfer[LJ[TRepr[a, N, r], C, TAIL]] = {

      create(a => {
        val x = tInf.mkAst(a.tail)
        val j = ast.LeftJoin(wt.value.name, cndInf.mkAst())
        ast.From(x.table, x.joins :+ j)
      })
    }

    implicit def rj[a, N, r <: HList, C <: JoinCond, TAIL <: FSH](
      implicit
      wt: Witness.Aux[N],
      ev: N <:< Symbol,
      cndInf: JoinCondInfer[C],
      tInf: FromInfer[TAIL]
    ): FromInfer[RJ[TRepr[a, N, r], C, TAIL]] = {

      create(a => {
        val x = tInf.mkAst(a.tail)
        val j = ast.RightJoin(wt.value.name, cndInf.mkAst())
        ast.From(x.table, x.joins :+ j)
      })
    }

    implicit def fj[a, N, r <: HList, C <: JoinCond, TAIL <: FSH](
      implicit
      wt: Witness.Aux[N],
      ev: N <:< Symbol,
      cndInf: JoinCondInfer[C],
      tInf: FromInfer[TAIL]
    ): FromInfer[FJ[TRepr[a, N, r], C, TAIL]] = {

      create(a => {
        val x = tInf.mkAst(a.tail)
        val j = ast.FullJoin(wt.value.name, cndInf.mkAst())
        ast.From(x.table, x.joins :+ j)
      })
    }

  }


  trait FromInferForStarSelect[A <: FSH] {
    type Out
    def mkAst(shape: A): ast.From
  }


  //TODO: flatten tuples?
  object FromInferForStarSelect {
    type Aux[A <: FSH,  Out0] = FromInferForStarSelect[A]  { type Out = Out0 }

    implicit def starFrom[A, N, r <: HList](
      implicit
      wt: Witness.Aux[N],
      ev: N <:< Symbol
    ):Aux[From[TRepr[A, N, r]], A] = {
      new FromInferForStarSelect[From[TRepr[A, N, r]]] {
        type Out = A
        def mkAst(shape: From[TRepr[A, N, r]]): ast.From = ast.From(wt.value.name, List.empty)
      }
    }

    implicit def starIJ[A, N, r <: HList, A2, N2, r2 <: HList, C <: JoinCond, tail <: FSH, O1](
      implicit
      wt: Witness.Aux[N],
      ev: N <:< Symbol,
      cndInf: JoinCondInfer[C],
      tInf: FromInferForStarSelect.Aux[tail, O1]
    ):Aux[IJ[TRepr[A, N, r], C, tail], (O1, A)] = {
      new FromInferForStarSelect[IJ[TRepr[A, N, r], C, tail]] {
        type Out = (O1, A)
        def mkAst(shape: IJ[TRepr[A, N, r], C, tail]): ast.From = {
          val x = tInf.mkAst(shape.tail)
          val j = ast.InnerJoin(wt.value.name, cndInf.mkAst())
          ast.From(x.table, x.joins :+ j)
        }
      }
    }

    implicit def starLJ[A, N, r <: HList, A2, N2, r2 <: HList, C <: JoinCond, tail <: FSH, O1, O2](
      implicit
      wt: Witness.Aux[N],
      ev: N <:< Symbol,
      cndInf: JoinCondInfer[C],
      tInf: FromInferForStarSelect.Aux[tail, O1],
      flTuple: TupleAppend.Aux[O1, Option[A], O2]
    //):Aux[LJ[TRepr[A, N, r], C, tail], (O1, Option[A])] = {
    ):Aux[LJ[TRepr[A, N, r], C, tail], O2] = {
      new FromInferForStarSelect[LJ[TRepr[A, N, r], C, tail]] {
        type Out = O2
        def mkAst(shape: LJ[TRepr[A, N, r], C, tail]): ast.From = {
          val x = tInf.mkAst(shape.tail)
          val j = ast.LeftJoin(wt.value.name, cndInf.mkAst())
          ast.From(x.table, x.joins :+ j)
        }
      }
    }

    implicit def starRJ[A, N, r <: HList, A2, N2, r2 <: HList, C <: JoinCond, tail <: FSH, O1](
      implicit
      wt: Witness.Aux[N],
      ev: N <:< Symbol,
      cndInf: JoinCondInfer[C],
      tInf: FromInferForStarSelect.Aux[tail, O1]
    ):Aux[RJ[TRepr[A, N, r], C, tail], (Option[O1], A)] = {
      new FromInferForStarSelect[RJ[TRepr[A, N, r], C, tail]] {
        type Out = (Option[O1], A)
        def mkAst(shape: RJ[TRepr[A, N, r], C, tail]): ast.From = {
          val x = tInf.mkAst(shape.tail)
          val j = ast.RightJoin(wt.value.name, cndInf.mkAst())
          ast.From(x.table, x.joins :+ j)
        }
      }
    }

    implicit def starFJ[A, N, r <: HList, A2, N2, r2 <: HList, C <: JoinCond, tail <: FSH, O1](
      implicit
      wt: Witness.Aux[N],
      ev: N <:< Symbol,
      cndInf: JoinCondInfer[C],
      tInf: FromInferForStarSelect.Aux[tail, O1]
    ):Aux[FJ[TRepr[A, N, r], C, tail], (Option[O1], Option[A])] = {
      new FromInferForStarSelect[FJ[TRepr[A, N, r], C, tail]] {
        type Out = (Option[O1], Option[A])
        def mkAst(shape: FJ[TRepr[A, N, r], C, tail]): ast.From = {
          val x = tInf.mkAst(shape.tail)
          val j = ast.FullJoin(wt.value.name, cndInf.mkAst())
          ast.From(x.table, x.joins :+ j)
        }
      }
    }
  }


}
