package typed.sql.internal

trait TupleAppend[A, B] {
  type Out
}

trait LowPrioTupleAppend {
  type Aux[A, B, Out0] = TupleAppend[A, B] { type Out = Out0 }

  implicit def forA[A, B]: Aux[A, B, (A, B)] = null
}

object TupleAppend extends TupleAppendInstances

