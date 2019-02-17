package typed.sql.internal

import shapeless._
import shapeless.labelled.FieldType
import shapeless.ops.record.Remover

/**
  * A - HList of FieldTypes
  * B - HList of symbols
  */
trait RemoveFields[A <: HList, B <: HList] {
  type Out1 <: HList
}

trait LowPrioRemoveFields {

  type Aux[A <: HList, B <: HList, Out0 <: HList] = RemoveFields[A, B] {type Out = Out0 }

}

object RemoveFields extends LowPrioRemoveFields  {

  implicit def hnil[A <: HList]: Aux[A, HNil, A] = null

  implicit def hCons1[A <: HList, H, T2 <: HList, K, v, ROut <: HList, TOut <: HList](
    implicit
    remover: Remover.Aux[A, H, (v, ROut)],
    next: RemoveFields.Aux[ROut, T2, TOut]
  ): Aux[A, H :: T2, TOut] = null


}
