package croodie

import croodie.internal.Join
import doobie.util.Read
import doobie.util.fragment.Fragment
import doobie.util.param.Param
import doobie.util.query.Query0
import doobie.util.update.Update0
import shapeless._
import shapeless.ops.record.Selector

sealed trait ExprTree extends Product with Serializable
final case class Eq[Q, V](q: Q, v: V) extends ExprTree
final case class Less[Q, V](q: Q, v: V) extends ExprTree
final case class Greater[Q, V](q: Q, v: V) extends ExprTree

final case class And[A, B](a: A, b: B) extends ExprTree
final case class Or[A, B](a: A, b: B) extends ExprTree

sealed trait LowExpr { self =>

  def render(tableName: String): String = self match {
    case LowExpr.Eq(f) => s"$tableName.$f = ?"
    case LowExpr.Less(f) => s"$tableName.$f$f < ?"
    case LowExpr.Greater(f) => s"$tableName.$f$f > ?"
    case LowExpr.And(a, b) => s"${a.render(tableName)} AND ${b.render(tableName)}"
    case LowExpr.Or(a, b) => s"${a.render(tableName)} OR ${b.render(tableName)}"
  }
}
object LowExpr {
  case class Eq(f: String) extends LowExpr
  case class Less(f: String) extends LowExpr
  case class Greater(f: String) extends LowExpr
  case class And(a: LowExpr, b: LowExpr) extends LowExpr
  case class Or(a: LowExpr, b: LowExpr) extends LowExpr
}

class Where[F <: HList, R, In](
//  select: Select[F, R],
  parentFr: Fragment,
  tableName: String,
  in: In,
  expr: LowExpr
) {

  def fr(p: Param[In]): Fragment = {
    val sql = " WHERE " + expr.render(tableName)
    val whereFr = Fragment[In](sql, in, None)(p.write)
    parentFr ++ whereFr
  }

  def query(implicit p: Param[In], read: Read[R]): Query0[R] = fr(p).query[R](read)
  def update(implicit p: Param[In]): Update0 = fr(p).update

}

case class WhereUnit[A](sql: String,  param: Param[A])
object WhereUnit {
  def eq[A](f: String, p: Param[A]): WhereUnit[A] = WhereUnit(s"$f = ?", p)
  def less[A](f: String, p: Param[A]): WhereUnit[A] = WhereUnit(s"$f < ?", p)
  def greater[A](f: String, p: Param[A]): WhereUnit[A] = WhereUnit(s"$f > ?", p)
}

/**
  * F - all table fields
  * Q - cmp expression tree
  * Out - what types need to put into fragment
  */
trait WhereInfer[F <: HList, Q] {
  type Out
  def expr: LowExpr
  def in(q: Q): Out
}

object WhereInfer {

  type Aux[F <: HList, Q, Out0] = WhereInfer[F, Q] { type Out = Out0 }

  implicit def eqOp[F <: HList, K, V, TT](
    implicit
    selector: Selector.Aux[F, K, TT],
    wt: Witness.Aux[K],
    ev: K <:< Symbol,
    ev2: V =:= TT
  ): Aux[F, Eq[K, V], V] = {
    new WhereInfer[F, Eq[K, V]] {
      type Out = V
      def expr: LowExpr = LowExpr.Eq(wt.value.name)
      def in(eq: Eq[K, V]): V = eq.v
    }
  }

  implicit def lessOp[F <: HList, K, V, TT](
    implicit
    selector: Selector.Aux[F, K, TT],
    wt: Witness.Aux[K],
    ev: K <:< Symbol,
    ev2: V =:= TT
  ): Aux[F, Less[K, V], V] = {
    new WhereInfer[F, Less[K, V]] {
      type Out = V
      def expr: LowExpr = LowExpr.Less(wt.value.name)
      def in(less: Less[K, V]): V = less.v
    }
  }

  implicit def greaterOp[F <: HList, K, V, TT](
    implicit
    selector: Selector.Aux[F, K, TT],
    wt: Witness.Aux[K],
    ev: K <:< Symbol,
    ev2: V =:= TT
  ): Aux[F, Greater[K, V], V] = {
    new WhereInfer[F, Greater[K, V]] {
      type Out = V
      def expr: LowExpr = LowExpr.Greater(wt.value.name)
      def in(gt: Greater[K, V]): V = gt.v
    }
  }

  implicit def andOp[F <: HList, A, B, AOut, BOut, Joined](
    implicit
    aInf: WhereInfer.Aux[F, A, AOut],
    bInf: WhereInfer.Aux[F, B, BOut],
    join: Join.Aux[AOut, BOut, Joined],
  ): Aux[F, And[A, B], Joined] = {
    new WhereInfer[F, And[A, B]] {
      type Out = Joined
      def expr: LowExpr = LowExpr.And(aInf.expr, bInf.expr)
      def in(and: And[A, B]): Joined = join(aInf.in(and.a), bInf.in(and.b))
    }
  }

  implicit def orOp[F <: HList, A, B, AOut, BOut, Joined](
    implicit
    aInf: WhereInfer.Aux[F, A, AOut],
    bInf: WhereInfer.Aux[F, B, BOut],
    join: Join.Aux[AOut, BOut, Joined],
  ): Aux[F, Or[A, B], Joined] = {
    new WhereInfer[F, Or[A, B]] {
      type Out = Joined
      def expr: LowExpr = LowExpr.Or(aInf.expr, bInf.expr)
      def in(or: Or[A, B]): Joined = join(aInf.in(or.a), bInf.in(or.b))
    }
  }

  def apply[F <: HList, A](implicit b: WhereInfer[F, A]): WhereInfer[F, A] = b
}

object cmp {

  def eqOp[V](wit: Witness)(v: V): Eq[wit.T, V] = Eq(wit.value, v)
  def lessOp[V](wit: Witness)(v: V): Less[wit.T, V] = Less(wit.value, v)
  def greaterOp[V](wit: Witness)(v: V): Greater[wit.T, V] = Greater(wit.value, v)

}
