package typed.sql.internal

import shapeless.ops.tuple.{Prepend, ReversePrepend}

trait FlattenTuple[A, B] {
  type Out
}

sealed trait LowPrioFlattenTuple {
  type Aux[A, B, Out0] = FlattenTuple[A, B] { type Out = Out0 }

  implicit def forA[A, B]: Aux[A, B, (A, B)] = null
}

object FlattenTuple extends LowPrioFlattenTuple {

  implicit def forTuple[A, B, O](
    implicit
    prepend: Prepend.Aux[B, A, O]
  ): Aux[A, B, O] = null
}
