package croodie

import shapeless._
import shapeless.labelled.FieldType
import shapeless.ops.hlist.Selector
import shapeless.{HList, HNil, Witness}


trait FieldSelector[In <: HList, A <: HList] {
  type Out <: HList
}

object FieldSelector {

  trait GetField[In <: HList, A] {
    type Out
  }

  trait LowPrioGetField {

    type Aux[In <: HList, A, Out0] = GetField[In, A] { type Out = Out0 }

    implicit def hListNotMatch[H, T <: HList, K, O](
      implicit
      next: GetField.Aux[T, K, O]
    ): Aux[H :: T, K, FieldType[K, O]] = new GetField[H :: T, K] {
      type Out = FieldType[K ,O]
    }
  }

  object GetField extends LowPrioGetField {

    implicit def hListMatch[T <: HList, K, V]: Aux[FieldType[K, V] :: T, K, FieldType[K, V]] = new GetField[FieldType[K, V] :: T, K] {
      type Out = FieldType[K ,V]
    }

    def apply[From <: HList, A](implicit v: GetField[From, A]): GetField[From, A] = v
  }


  type Aux[In <: HList, A <: HList, Out0 <: HList] = FieldSelector[In, A] { type Out = Out0 }

  implicit def hNil[From <: HList]: Aux[From, HNil, HNil] = new FieldSelector[From, HNil] {
    type Out = HNil
  }

  implicit def hList[From <: HList, H, K, V, T <: HList, N <: HList](
    implicit
    getField: GetField.Aux[From, H, FieldType[K, V]],
    next: FieldSelector.Aux[From, T, N]
  ): Aux[From, H :: T, FieldType[K, V] :: N] = {
    new FieldSelector[From, H :: T] {
      type Out = FieldType[K, V] :: N
    }
  }

  def apply[From <: HList, A <: HList](implicit v: FieldSelector[From, A]): FieldSelector[From, A] = v
}
