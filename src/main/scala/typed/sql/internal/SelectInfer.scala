package typed.sql.internal

import shapeless._
import shapeless.ops.record.Selector
import typed.sql.{All, Column, Table}

trait SelectInfer[S, R <: HList, Q] {
  type Out
  def fields(t: Table.Aux[S, R]): List[String]
}
trait LowPrioSelectInfer {

  type Aux[S, R <: HList, Q, Out0] = SelectInfer[S, R, Q] { type Out = Out0 }

  implicit def hnil[S, R <: HList]: Aux[S, R, HNil, HNil] = new SelectInfer[S, R, HNil]{
    type Out = HNil
    def fields(t: Table.Aux[S, R]): List[String] = List.empty
  }

  implicit def hCons[S, R <: HList, K, V, T <: HList, NOut <: HList](
    implicit
    selector: Selector.Aux[R, K, V],
    wt: Witness.Aux[K],
    ev: K <:< Symbol,
    next: SelectInfer.Aux[S, R, T, NOut]
  ): Aux[S, R, Column[K, V] :: T, V :: NOut] = {
    new SelectInfer[S, R, Column[K, V] :: T] {
      type Out = V :: NOut
      def fields(t: Table.Aux[S, R]): List[String] = wt.value.name :: next.fields(t)
    }
  }

}

object SelectInfer extends LowPrioSelectInfer {

  implicit def forStar[S, R <: HList, O <: HList]: Aux[S, R, All.type :: HNil, S] = {
    new SelectInfer[S, R, All.type :: HNil] {
      type Out = S
      def fields(t: Table.Aux[S, R]): List[String] = t.columns
    }
  }
}

