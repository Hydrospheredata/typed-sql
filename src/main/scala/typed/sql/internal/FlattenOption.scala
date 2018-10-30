package typed.sql.internal

trait FlattenOption[A] {
  type Out
}

trait LowPrioFlattenOption {

  type Aux[A, Out0] = FlattenOption[A] { type Out = Out0 }

  implicit def forA[A]: Aux[A, Option[A]] = null
}

object FlattenOption extends LowPrioFlattenOption {

  implicit def forOpt[A]: Aux[Option[A], Option[A]] = null
}
