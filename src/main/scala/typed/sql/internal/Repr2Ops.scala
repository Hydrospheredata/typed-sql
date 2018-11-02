package typed.sql.internal

import shapeless.ops.record.Selector
import shapeless._
import typed.sql.{Assign, ast}

object Repr2Ops {

  trait InferUpdateSet[R <: HList, In <: HList] {
    type Out
    def mkAst: List[ast.Set]
    def out(in: In): Out
  }

  object InferUpdateSet {

    type Aux[R <: HList, In <: HList, Out0] = InferUpdateSet[R, In] { type Out = Out0 }

    implicit def hnil[R <: HList]: Aux[R, HNil, HNil] =
      new InferUpdateSet[R, HNil] {
        type Out = HNil
        def mkAst: List[ast.Set] = List.empty
        def out(in: HNil): HNil = HNil
      }

    implicit def hCons[R <: HList, K, V, N, T <: HList, Out1 <: HList](
      implicit
      sel: Selector.Aux[R, K, V],
      next: InferUpdateSet.Aux[R, T, Out1],
      wt1: Witness.Aux[K],
      ev1: K <:< Symbol,
      wt2: Witness.Aux[N],
      ev2: N <:< Symbol,
    ): Aux[R, Assign[K, V, N] :: T, V :: Out1] =
      new InferUpdateSet[R, Assign[K, V, N] :: T] {
        type Out = V :: Out1
        def mkAst: List[ast.Set] = ast.Set(ast.Col(wt2.value.name, wt1.value.name)) :: next.mkAst
        def out(in: Assign[K, V, N] :: T): V :: Out1 =  in.head.v :: next.out(in.tail)
      }
  }

}
