package typed.sql

sealed trait WhereCond extends Product with Serializable
object WhereCond {
  final case class Eq[C, V](c: C, v: V) extends WhereCond
  final case class Less[K, V](v: V) extends WhereCond
  final case class LessOrEq[K, V](v: V) extends WhereCond
  final case class Gt[K, V](v: V) extends WhereCond
  final case class GtOrEq[K, V](v: V) extends WhereCond
  final case class Like[K](v: String) extends WhereCond
  final case class In[K, V](v: List[V]) extends WhereCond

  final case class And[A, B](a: A, b: B) extends WhereCond
  final case class Or[A, B](a: A, b: B) extends WhereCond
}

sealed trait JoinCond
object JoinCond {

  final case class Eq[K1, V, T1, K2, T2](
    c1: Column[K1, V, T1],
    c2: Column[K2, V, T2]
  ) extends JoinCond

  final case class And[A <: JoinCond, B <: JoinCond](a: A, b: B) extends JoinCond
  final case class Or[A <: JoinCond, B <: JoinCond](a: A, b: B) extends JoinCond
}
