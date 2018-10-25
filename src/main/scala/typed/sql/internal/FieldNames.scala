package typed.sql.internal

import shapeless.labelled.FieldType
import shapeless.{HList, HNil, Witness, _}

trait FieldNames[A <: HList] {
  def apply(): List[String]
}

object FieldNames {

  implicit val hNil: FieldNames[HNil] = new FieldNames[HNil] {
    override def apply(): List[String] = List.empty
  }

  implicit def hCons[T <: HList, K, V](
    implicit
    wit: Witness.Aux[K],
    ev2: K <:< Symbol,
    next: FieldNames[T]
  ): FieldNames[FieldType[K, V] :: T] = {
    new FieldNames[FieldType[K, V] :: T] {
      override def apply(): List[String] =  wit.value.name :: next()
    }
  }

  def apply[A <: HList](implicit fN: FieldNames[A]): FieldNames[A] = fN
}

