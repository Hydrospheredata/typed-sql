package croodie

import shapeless._
import shapeless.labelled.FieldType

trait FieldTypes[A <: HList] {
  type Out <: HList
}

object FieldTypes {
  type Aux[A <: HList, Out0 <: HList] = FieldTypes[A] {type Out = Out0}

  implicit val hNil: Aux[HNil, HNil] = null
  implicit def hCons[K, V, T <: HList, O <: HList](
    implicit next: FieldTypes.Aux[T, O]
  ): Aux[FieldType[K, V] :: T, V :: O] = null
}
