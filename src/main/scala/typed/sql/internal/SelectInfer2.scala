package typed.sql.internal

trait SelectInfer2[From, Q] {
  type Out
}

object SelectInfer2 {
  type Aux[From, Q, Out0] = SelectInfer2[From, Q] {type Out = Out0}


}
