package typed.sql.internal

import shapeless.HList
import shapeless.ops.record.Selector

trait GetColumn[R, K] {
  type Out
}

object GetColumn {
  type Aux[R, K, V] = GetColumn[R, K] { type Out = V }
  implicit def record[R <: HList, K, V](implicit sel: Selector.Aux[R, K, V]): Aux[R, K, V] = null
}
