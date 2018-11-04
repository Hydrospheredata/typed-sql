package typed.sql

import WhereClause._
import cats.data.NonEmptyList

sealed trait ColumnQuery

/**
  * select *
  *
  */
sealed trait All extends ColumnQuery
case object All extends All


sealed trait CLN[T] extends ColumnQuery
case class Column[K, V, T] private[sql](k: K, t: T) extends CLN[T] { self =>

  def `===`(v: V): Eq[K, V, T] = Eq(v)
  def `>`(v: V): Gt[K, V, T] = Gt(v)
  def `>=`(v: V): GtOrEq[K, V, T] = GtOrEq(v)
  def `<`(v: V): Less[K, V, T] = Less(v)
  def `=<`(v: V): LessOrEq[K, V, T] = LessOrEq(v)
  def like(v: V)(implicit ev: V =:= String): Like[K, T] = Like(v)
  def in(values: NonEmptyList[V]): In[K, V, T] = In(values.toList)

  def `<==>`[K2, T2](c2: Column[K2, V, T2]): JoinCond.Eq[K, V, T, K2, T2] = JoinCond.Eq(self, c2)

  def `:=`(v: V): Assign[K, V, T] = Assign(v)

  def ASC: ASC[K, V, T] = new ASC[K, V, T]
  def DESC: DESC[K, V, T] = new DESC[K, V, T]
}

case class Assign[K, V, T](v: V)

sealed trait SortOrder
final class ASC[K, V, T] extends SortOrder
final class DESC[K, V, T] extends SortOrder

trait ColumnSyntax {

  implicit class CmpOpChain[A <: WhereClause](a: A) {
    def and[B <: WhereClause](b: B): And[A, B] = And(a, b)
    def or[B <: WhereClause](b: B): Or[A, B] = Or(a, b)
  }
}
