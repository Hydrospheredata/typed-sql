package typed.sql.internal

import cats.data.NonEmptyList
import shapeless._
import shapeless.ops.adjoin.Adjoin
import typed.sql.internal.FSHOps.IsFieldOf
import typed.sql.{FSH, WhereCond, ast}

import scala.reflect.ClassTag

trait WhereInfer[A <: FSH, C] {
  type Out
  def mkAst(c: C): ast.WhereCond
  def out(c: C): Out
}

object WhereInfer {

  type Aux[A <: FSH, C, Out0] = WhereInfer[A, C] { type Out = Out0 }

  implicit def forEq[A <: FSH, K, V, T](
    implicit
    wt1: Witness.Aux[T],
    ev1: T <:< Symbol,
    wt2: Witness.Aux[K],
    ev2: K <:< Symbol,
    evev: IsFieldOf[A, T]
  ): Aux[A, WhereCond.Eq[K, V, T], V :: HNil] = {
    new WhereInfer[A, WhereCond.Eq[K, V, T]] {
      type Out = V :: HNil
      def mkAst(c: WhereCond.Eq[K, V, T]): ast.WhereCond = ast.WhereEq(ast.Col(wt1.value.name, wt2.value.name))
      def out(c: WhereCond.Eq[K, V, T]): V :: HNil = c.v :: HNil
    }
  }

  implicit def forLess[A <: FSH, K, V, T](
    implicit
    wt1: Witness.Aux[T],
    ev1: T <:< Symbol,
    wt2: Witness.Aux[K],
    ev2: K <:< Symbol,
    evev: IsFieldOf[A, T]
  ): Aux[A, WhereCond.Less[K, V, T], V :: HNil] = {
    new WhereInfer[A, WhereCond.Less[K, V, T]] {
      type Out = V :: HNil
      def mkAst(c: WhereCond.Less[K, V, T]): ast.WhereCond = ast.Less(ast.Col(wt1.value.name, wt2.value.name))
      def out(c: WhereCond.Less[K, V, T]): V :: HNil = c.v :: HNil
    }
  }

  implicit def forLessOrEq[A <: FSH, K, V, T](
    implicit
    wt1: Witness.Aux[T],
    ev1: T <:< Symbol,
    wt2: Witness.Aux[K],
    ev2: K <:< Symbol,
    evev: IsFieldOf[A, T]
  ): Aux[A, WhereCond.LessOrEq[K, V, T], V :: HNil] = {
    new WhereInfer[A, WhereCond.LessOrEq[K, V, T]] {
      type Out = V :: HNil
      def mkAst(c: WhereCond.LessOrEq[K, V, T]): ast.WhereCond = ast.LessOrEq(ast.Col(wt1.value.name, wt2.value.name))
      def out(c: WhereCond.LessOrEq[K, V, T]): V :: HNil = c.v :: HNil
    }
  }

  implicit def forGt[A <: FSH, K, V, T](
    implicit
    wt1: Witness.Aux[T],
    ev1: T <:< Symbol,
    wt2: Witness.Aux[K],
    ev2: K <:< Symbol,
    evev: IsFieldOf[A, T]
  ): Aux[A, WhereCond.Gt[K, V, T], V :: HNil] = {
    new WhereInfer[A, WhereCond.Gt[K, V, T]] {
      type Out = V :: HNil
      def mkAst(c: WhereCond.Gt[K, V, T]): ast.WhereCond = ast.Gt(ast.Col(wt1.value.name, wt2.value.name))
      def out(c: WhereCond.Gt[K, V, T]): V :: HNil = c.v :: HNil
    }
  }

  implicit def forGtOrEq[A <: FSH, K, V, T](
    implicit
    wt1: Witness.Aux[T],
    ev1: T <:< Symbol,
    wt2: Witness.Aux[K],
    ev2: K <:< Symbol,
    evev: IsFieldOf[A, T]
  ): Aux[A, WhereCond.GtOrEq[K, V, T], V :: HNil] = {
    new WhereInfer[A, WhereCond.GtOrEq[K, V, T]] {
      type Out = V :: HNil
      def mkAst(c: WhereCond.GtOrEq[K, V, T]): ast.WhereCond = ast.GtOrEq(ast.Col(wt1.value.name, wt2.value.name))
      def out(c: WhereCond.GtOrEq[K, V, T]): V :: HNil = c.v :: HNil
    }
  }

  implicit def forLike[A <: FSH, K, T](
    implicit
    wt1: Witness.Aux[T],
    ev1: T <:< Symbol,
    wt2: Witness.Aux[K],
    ev2: K <:< Symbol,
    evev: IsFieldOf[A, T]
  ): Aux[A, WhereCond.Like[K, T], String :: HNil] = {
    new WhereInfer[A, WhereCond.Like[K, T]] {
      type Out = String :: HNil
      def mkAst(c: WhereCond.Like[K, T]): ast.WhereCond = ast.Like(ast.Col(wt1.value.name, wt2.value.name))
      def out(c: WhereCond.Like[K, T]): String :: HNil = c.v :: HNil
    }
  }

  implicit def forIn[A <: FSH, K, V, T](
    implicit
    wt1: Witness.Aux[T],
    ev1: T <:< Symbol,
    wt2: Witness.Aux[K],
    ev2: K <:< Symbol,
    evev: IsFieldOf[A, T],
    ct: ClassTag[V]
  ): Aux[A, WhereCond.In[K, V, T], List[V] :: HNil] = {
    new WhereInfer[A, WhereCond.In[K, V, T]] {
      type Out = List[V] :: HNil
      def mkAst(c: WhereCond.In[K, V, T]): ast.WhereCond = ast.In(ast.Col(wt1.value.name, wt2.value.name), c.v.size)
      def out(c: WhereCond.In[K, V, T]): List[V] :: HNil = c.v :: HNil
    }
  }

  implicit def forAnd[S <: FSH, A, B, AOut, BOut, Joined <: HList](
    implicit
    aInf: WhereInfer.Aux[S, A, AOut],
    bInf: WhereInfer.Aux[S, B, BOut],
    adjoin: Adjoin.Aux[AOut :: BOut :: HNil, Joined]
  ): Aux[S, WhereCond.And[A, B], Joined] = {
    new WhereInfer[S, WhereCond.And[A, B]] {
      type Out = Joined
      def mkAst(c: WhereCond.And[A, B]): ast.WhereCond = ast.And(aInf.mkAst(c.a), bInf.mkAst(c.b))
      def out(c: WhereCond.And[A, B]): Joined = adjoin(aInf.out(c.a) :: bInf.out(c.b) :: HNil)
    }
  }

  implicit def forOr[S <: FSH, A, B, AOut, BOut, Joined <: HList](
    implicit
    aInf: WhereInfer.Aux[S, A, AOut],
    bInf: WhereInfer.Aux[S, B, BOut],
    adjoin: Adjoin.Aux[AOut :: BOut :: HNil, Joined]
  ): Aux[S, WhereCond.Or[A, B], Joined] = {
    new WhereInfer[S, WhereCond.Or[A, B]] {
      type Out = Joined
      def mkAst(c: WhereCond.Or[A, B]): ast.WhereCond = ast.Or(aInf.mkAst(c.a), bInf.mkAst(c.b))
      def out(c: WhereCond.Or[A, B]): Joined = adjoin(aInf.out(c.a) :: bInf.out(c.b) :: HNil)
    }
  }

}
