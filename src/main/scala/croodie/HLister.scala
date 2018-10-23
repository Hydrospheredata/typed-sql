package croodie

import shapeless._

/**
  *  Converts Any or Tuple into HList
  */
trait HLister[A] {
  type Out
  def apply(a: A): Out
}

trait LowerPriorityHLister {

  type Aux[A, Out0] = HLister[A] {type Out = Out0 }

  implicit def forAny[A]: Aux[A, A :: HNil] = new HLister[A] {
    override type Out = A :: HNil
    override def apply(a: A): A :: HNil = a :: HNil
  }

  def apply[A](implicit h: HLister[A]): HLister[A] = h
}

object HLister extends HListerInstances

