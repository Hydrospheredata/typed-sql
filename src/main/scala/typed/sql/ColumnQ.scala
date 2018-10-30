package typed.sql

import WhereClause._

sealed trait ColumnQuery

/**
  * select *
  *
  */
sealed trait All extends ColumnQuery
case object All extends All


sealed trait CLN[T] extends ColumnQuery
case class Column2[K, V, T] private[sql](k: K, t: T) extends CLN[T] { self =>

  def `====`(v: V): Eq[K, V, T] = Eq(v)
  def `>`(v: V): Gt[K, V, T] = Gt(v)
  def `>=`(v: V): GtOrEq[K, V, T] = GtOrEq(v)
  def `<`(v: V): Less[K, V, T] = Less(v)
  def `<=`(v: V): LessOrEq[K, V, T] = LessOrEq(v)
  def like(v: V)(implicit ev: V =:= String): Like[K, T] = Like(v)

  def `<==>`[K2, T2](c2: Column2[K2, V, T2]): JoinCond.Eq[K, V, T, K2, T2] = JoinCond.Eq(self, c2)
}

trait ColumnSyntax {

  implicit class CmpOpChain[A <: WhereClause](a: A) {
    def and[B <: WhereClause](b: B): And[A, B] = And(a, b)
    def or[B <: WhereClause](b: B): Or[A, B] = Or(a, b)
  }
}
