package croodie

import shapeless._
import shapeless.labelled.FieldType

trait CollectFields[In <: HList] {
  type Out <: HList
}

object CollectFields {
  type Aux[In <: HList, Out0 <: HList] = CollectFields[In] { type Out = Out0 }

  implicit val hNil: Aux[HNil, HNil] = new CollectFields[HNil] { type Out = HNil }
  implicit def hCons[H, T <: HList, K, V, O <: HList](
    implicit
    ev: H <:< FieldType[K, V],
    next: CollectFields.Aux[T, O]
  ): Aux[H :: T, FieldType[K, V] :: O] = new CollectFields[H :: T] { type Out = FieldType[K, V] :: O}
}
