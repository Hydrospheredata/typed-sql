package poc.internal

import shapeless._
import shapeless.ops.adjoin.Adjoin

trait Join[A, B] {
  type Out <: HList
  def apply(a: A, b: B): Out
}

object Join {

  type Aux[A, B, Out0] = Join[A, B] { type Out = Out0 }

  implicit def simple[A, B, O <: HList](
    implicit
    adjoin: Adjoin.Aux[A :: B :: HNil, O]
  ): Aux[A, B, O] = new Join[A, B] {
    type Out = O
    def apply(a: A, b: B): O = adjoin(a :: b :: HNil)
  }

  def apply[A, B](implicit j: Join[A, B]): Join[A, B] = j
}
