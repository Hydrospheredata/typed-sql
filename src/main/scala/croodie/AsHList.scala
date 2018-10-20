package croodie

import shapeless._

trait AsHList[A] {
  type Out
}

trait LowPriorityAsHList {
  type Aux[A, Out0] = AsHList[A] {type Out= Out0 }

//  implicit def single[A]: Aux[A, A :: HNil] = new AsHList[A] {
//    type Out = A :: HNil
//  }
}

object AsHList extends LowPriorityAsHList {

  implicit def tuple[A, H <: HList](implicit gen: Generic.Aux[A, H]): Aux[A, H] = new AsHList[A] {
    type Out = H
  }

}

