package typed.sql.internal

import shapeless.labelled.FieldType
import shapeless.{HList, HNil, Witness, _}

trait FieldNames[A] {
  def apply(): List[String]
}

object FieldNames {

  implicit val hNil: FieldNames[HNil] = new FieldNames[HNil] {
    override def apply(): List[String] = List.empty
  }

  implicit def hCons[T <: HList, K, V](
    implicit
    s2s: Symbol2Str[K],
    next: FieldNames[T]
  ): FieldNames[FieldType[K, V] :: T] = {
    new FieldNames[FieldType[K, V] :: T] {
      override def apply(): List[String] =  s2s.str :: next()
    }
  }

  def apply[A <: HList](implicit fN: FieldNames[A]): FieldNames[A] = fN
}

