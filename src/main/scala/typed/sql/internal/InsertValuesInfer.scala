package typed.sql.internal

import shapeless._
import shapeless.labelled.FieldType
import shapeless.ops.record.Values
import typed.sql.{TR, ast}

trait InsertValuesInfer[A, V] {
  def columns: List[String]
}

trait CheckHTypes[A, B]
object CheckHTypes {
  implicit val hNil: CheckHTypes[HNil, HNil] = null
  implicit def hCons[H1, H2, T1 <: HList,T2 <: HList](
    implicit ev: H2 <:< H1
  ): CheckHTypes[H1 :: T1, H2 :: T2] = null
}

object InsertValuesInfer {

  implicit def labelledRepr[A <: HList, Vs <: HList, In <: HList](
    implicit
    vs: Values.Aux[A, Vs],
    check: CheckHTypes[Vs, In],
    fieldNames: FieldNames[A]
  ): InsertValuesInfer[A, In] = {
    new InsertValuesInfer[A, In] {
      val columns = fieldNames()
    }
  }
}

