package typed.sql

import WhereCond._
import cats.data.NonEmptyList

sealed trait ColumnQuery

/**
  * select *
  *
  */
sealed trait All extends ColumnQuery
case object All extends All

final case class Column[K, V, T] private[sql](k: K, tableName: String) { self =>

  def `===`(v: V): Eq[Column[K, V, T], V] = Eq(self, v)
  def `>`(v: V): Gt[K, V] = Gt(v)
  def `>=`(v: V): GtOrEq[K, V] = GtOrEq(v)
  def `<`(v: V): Less[K, V] = Less(v)
  def `=<`(v: V): LessOrEq[K, V] = LessOrEq(v)
  def like(v: V)(implicit ev: V =:= String): Like[K] = Like(v)
  def in(values: NonEmptyList[V]): In[K, V] = In(values.toList)

  def `<==>`[K2, T2](c2: Column[K2, V, T2]): JoinCond.Eq[K, V, T, K2, T2] = JoinCond.Eq(self, c2)

  def `:=`(v: V): Assign[K, V, T] = Assign(v)

  def ASC: ASC[K, V, T] = new ASC[K, V, T]
  def DESC: DESC[K, V, T] = new DESC[K, V, T]
}

final case class Assign[K, V, T](v: V)

sealed trait SortOrder
final class ASC[K, V, T] extends SortOrder
final class DESC[K, V, T] extends SortOrder

trait ColumnSyntax {

  implicit class CmpOpChain[A <: WhereCond](a: A) {
    def and[B <: WhereCond](b: B): And[A, B] = And(a, b)
    def or[B <: WhereCond](b: B): Or[A, B] = Or(a, b)
  }
}
