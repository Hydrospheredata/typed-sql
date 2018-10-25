package typed.sql

sealed trait WhereClause
object WhereClause {

  final case class Eq[Q, V](q: Q, v: V) extends WhereClause
  final case class Less[Q, V](q: Q, v: V) extends WhereClause
  final case class LessOrEq[Q, V](q: Q, v: V) extends WhereClause
  final case class Gt[Q, V](q: Q, v: V) extends WhereClause
  final case class GtOrEq[Q, V](q: Q, v: V) extends WhereClause
  final case class Like[Q](q: Q, v: String) extends WhereClause

  final case class And[A, B](a: A, b: B) extends WhereClause
  final case class Or[A, B](a: A, b: B) extends WhereClause

}
