package typed.sql

import WhereClause._

sealed trait ColumnQuery
case object All extends ColumnQuery

case class Column[K, V] private[sql](k: K) extends ColumnQuery {

  def `====`(v: V): Eq[K, V] = Eq(k, v)
  def `>`(v: V): Gt[K, V] = Gt(k, v)
  def `>=`(v: V):GtOrEq[K, V] = GtOrEq(k, v)
  def `<`(v: V): Less[K, V] = Less(k, v)
  def `<=`(v: V): LessOrEq[K, V] = LessOrEq(k, v)
  def like(v: V)(implicit ev: V =:= String): Like[K] = Like(k, v)

}

trait ColumnSyntax {

  implicit class CmpOpChain[A <: WhereClause](a: A) {

    def and[B <: WhereClause](b: B): And[A, B] = And(a, b)
    def or[B <: WhereClause](b: B): Or[A, B] = Or(a, b)
  }
}
