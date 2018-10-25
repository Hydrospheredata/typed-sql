package typed.sql.internal

import shapeless._
import shapeless.ops.record.Selector
import typed.sql.WhereClause._

sealed trait LowExpr { self =>
  def render(tableName: String): String = self match {
    case LowExpr.Eq(f) => s"$tableName.$f = ?"
    case LowExpr.Less(f) => s"$tableName.$f < ?"
    case LowExpr.LessOrEq(f) => s"$tableName.$f <= ?"
    case LowExpr.Gt(f) => s"$tableName.$f > ?"
    case LowExpr.GtOrEq(f) => s"$tableName.$f >= ?"
    case LowExpr.Like(f) => s"$tableName.$f like ?"
    case LowExpr.And(a, b) => s"${a.render(tableName)} AND ${b.render(tableName)}"
    case LowExpr.Or(a, b) => s"${a.render(tableName)} OR ${b.render(tableName)}"
  }
}

object LowExpr {
  final case class Eq(f: String) extends LowExpr
  final case class Less(f: String) extends LowExpr
  final case class LessOrEq(f: String) extends LowExpr
  final case class Gt(f: String) extends LowExpr
  final case class GtOrEq(f: String) extends LowExpr
  final case class Like(f: String) extends LowExpr
  final case class And(a: LowExpr, b: LowExpr) extends LowExpr
  final case class Or(a: LowExpr, b: LowExpr) extends LowExpr
}


trait WhereInfer[R <: HList, Q] {
  type Out <: HList
  def expr: LowExpr
  def in(q: Q): Out
}

object WhereInfer {

  type Aux[R <: HList, Q, Out0] = WhereInfer[R, Q] { type Out = Out0 }

  private def create[R <: HList, Q, Out1 <: HList](exprV: LowExpr, f: Q => Out1): WhereInfer.Aux[R, Q, Out1] = {
    new WhereInfer[R, Q] {
      type Out = Out1
      val expr = exprV
      def in(q: Q) = f(q)
    }
  }

  implicit def eqOp[F <: HList, K, V, TT](
    implicit
    selector: Selector.Aux[F, K, TT],
    wt: Witness.Aux[K],
    ev: K <:< Symbol,
    ev2: V =:= TT
  ): Aux[F, Eq[K, V], V :: HNil] = create(LowExpr.Eq(wt.value.name), e => e.v :: HNil)

  implicit def lessOp[F <: HList, K, V, TT](
    implicit
    selector: Selector.Aux[F, K, TT],
    wt: Witness.Aux[K],
    ev: K <:< Symbol,
    ev2: V =:= TT
  ): Aux[F, Less[K, V], V :: HNil] = create(LowExpr.Less(wt.value.name), _.v :: HNil)

  implicit def lessOrEqOp[F <: HList, K, V, TT](
    implicit
    selector: Selector.Aux[F, K, TT],
    wt: Witness.Aux[K],
    ev: K <:< Symbol,
    ev2: V =:= TT
  ): Aux[F, LessOrEq[K, V], V :: HNil] = create(LowExpr.LessOrEq(wt.value.name), _.v :: HNil)

  implicit def gtOp[F <: HList, K, V, TT](
    implicit
    selector: Selector.Aux[F, K, TT],
    wt: Witness.Aux[K],
    ev: K <:< Symbol,
    ev2: V =:= TT
  ): Aux[F, Gt[K, V], V :: HNil] = create(LowExpr.Gt(wt.value.name), _.v :: HNil)

  implicit def gtOrEqOp[F <: HList, K, V, TT](
    implicit
    selector: Selector.Aux[F, K, TT],
    wt: Witness.Aux[K],
    ev: K <:< Symbol,
    ev2: V =:= TT
  ): Aux[F, GtOrEq[K, V], V :: HNil] = create(LowExpr.Gt(wt.value.name), _.v :: HNil)

  implicit def like[F <: HList, K, V, TT](
    implicit
    selector: Selector.Aux[F, K, TT],
    wt: Witness.Aux[K],
    ev: K <:< Symbol,
    ev2: String =:= TT,
  ): Aux[F, Like[K], String :: HNil] = create(LowExpr.Like(wt.value.name), _.v :: HNil)

  implicit def andOp[F <: HList, A, B, AOut, BOut, Joined <: HList](
    implicit
    aInf: WhereInfer.Aux[F, A, AOut],
    bInf: WhereInfer.Aux[F, B, BOut],
    join: Join.Aux[AOut, BOut, Joined],
  ): Aux[F, And[A, B], Joined] =
    create(LowExpr.And(aInf.expr, bInf.expr), and => join(aInf.in(and.a), bInf.in(and.b)))

  implicit def orOp[F <: HList, A, B, AOut, BOut, Joined <: HList](
    implicit
    aInf: WhereInfer.Aux[F, A, AOut],
    bInf: WhereInfer.Aux[F, B, BOut],
    join: Join.Aux[AOut, BOut, Joined],
  ): Aux[F, Or[A, B], Joined] = create(LowExpr.Or(aInf.expr, bInf.expr), or => join(aInf.in(or.a), bInf.in(or.b)))


  def apply[F <: HList, A](implicit b: WhereInfer[F, A]): WhereInfer[F, A] = b
}
