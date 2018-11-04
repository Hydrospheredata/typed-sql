package typed.sql

import cats.data.NonEmptyList

sealed trait WhereClause extends Product with Serializable

object WhereClause {

  final case class Eq[K, V, T](v: V) extends WhereClause
  final case class Less[K, V, T](v: V) extends WhereClause
  final case class LessOrEq[K, V, T](v: V) extends WhereClause
  final case class Gt[K, V, T](v: V) extends WhereClause
  final case class GtOrEq[K, V, T](v: V) extends WhereClause
  final case class Like[K, T](v: String) extends WhereClause
  final case class In[K, V, T](v: List[V]) extends WhereClause

  final case class And[A, B](a: A, b: B) extends WhereClause
  final case class Or[A, B](a: A, b: B) extends WhereClause

}
