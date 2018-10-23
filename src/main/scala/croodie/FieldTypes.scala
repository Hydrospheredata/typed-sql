package croodie

import shapeless._
import shapeless.labelled.FieldType
import shapeless.ops.hlist.Selector

trait FieldTypes[A <: HList] {
  type Out <: HList
}

object FieldTypes {
  type Aux[A <: HList, Out0 <: HList] = FieldTypes[A] {type Out = Out0}

  implicit val hNil: Aux[HNil, HNil] = new FieldTypes[HNil] { type Out = HNil }
  implicit def hCons[K, V, T <: HList, O <: HList, X](
    implicit
    wit: Witness.Aux[K],
    select: Selector[T, K],
    next: FieldTypes.Aux[T, O]
  ): Aux[FieldType[K, V] :: T, X :: O] = new FieldTypes[FieldType[K, V] :: T] {
    type Out = X :: O
  }
}
