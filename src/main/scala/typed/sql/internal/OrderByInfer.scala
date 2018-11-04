package typed.sql.internal

import shapeless._
import shapeless.ops.hlist.IsHCons
import typed.sql.internal.FSHOps.IsFieldOf
import typed.sql._

trait OrderByInfer[A <: FSH, Q] {
  def columns: List[(ast.Col, ast.SortOrder)]
}

object OrderByInfer {

  implicit def lastClmn[A <: FSH, K, v, N](
    implicit
    isFieldOf: IsFieldOf[A, N],
    wt1: Witness.Aux[N],
    ev1: N <:< Symbol,
    wt2: Witness.Aux[K],
    ev2: K <:< Symbol
  ): OrderByInfer[A, Column[K, v, N] :: HNil] =
    new OrderByInfer[A, Column[K, v, N] :: HNil] {
      val columns: List[(ast.Col, ast.SortOrder)] = List(ast.Col(wt1.value.name, wt2.value.name) -> ast.ASC)
    }

  implicit def lastASC[A <: FSH, K, v, N](
    implicit
    isFieldOf: IsFieldOf[A, N],
    wt1: Witness.Aux[N],
    ev1: N <:< Symbol,
    wt2: Witness.Aux[K],
    ev2: K <:< Symbol
  ): OrderByInfer[A, ASC[K, v, N] :: HNil] =
    new OrderByInfer[A, ASC[K, v, N] :: HNil] {
      val columns: List[(ast.Col, ast.SortOrder)] = List(ast.Col(wt1.value.name, wt2.value.name) -> ast.ASC)
    }

  implicit def lastDESC[A <: FSH, K, v, N](
    implicit
    isFieldOf: IsFieldOf[A, N],
    wt1: Witness.Aux[N],
    ev1: N <:< Symbol,
    wt2: Witness.Aux[K],
    ev2: K <:< Symbol
  ): OrderByInfer[A, DESC[K, v, N] :: HNil] =
    new OrderByInfer[A, DESC[K, v, N] :: HNil] {
      val columns: List[(ast.Col, ast.SortOrder)] = List(ast.Col(wt1.value.name, wt2.value.name) -> ast.DESC)
    }

  implicit def hConsClmn[A <: FSH,T <: HList, K, v, N](
    implicit
    isFieldOf: IsFieldOf[A, N],
    isHCons1: IsHCons[T],
    next: OrderByInfer[A, T],
    wt1: Witness.Aux[N],
    ev1: N <:< Symbol,
    wt2: Witness.Aux[K],
    ev2: K <:< Symbol
  ): OrderByInfer[A, Column[K, v, N] :: T] =
    new OrderByInfer[A, Column[K, v, N] :: T] {
      val columns: List[(ast.Col, ast.SortOrder)] = ast.Col(wt1.value.name, wt2.value.name) -> ast.ASC :: next.columns
    }

  implicit def hConsASC[A <: FSH,T <: HList, K, v, N](
    implicit
    isFieldOf: IsFieldOf[A, N],
    isHCons1: IsHCons[T],
    next: OrderByInfer[A, T],
    wt1: Witness.Aux[N],
    ev1: N <:< Symbol,
    wt2: Witness.Aux[K],
    ev2: K <:< Symbol
  ): OrderByInfer[A, ASC[K, v, N] :: T] =
    new OrderByInfer[A, ASC[K, v, N] :: T] {
      val columns: List[(ast.Col, ast.SortOrder)] = ast.Col(wt1.value.name, wt2.value.name) -> ast.ASC :: next.columns
    }

  implicit def hConsDESC[A <: FSH,T <: HList, K, v, N](
    implicit
    isFieldOf: IsFieldOf[A, N],
    isHCons1: IsHCons[T],
    next: OrderByInfer[A, T],
    wt1: Witness.Aux[N],
    ev1: N <:< Symbol,
    wt2: Witness.Aux[K],
    ev2: K <:< Symbol
  ): OrderByInfer[A, DESC[K, v, N] :: T] =
    new OrderByInfer[A, DESC[K, v, N] :: T] {
      val columns: List[(ast.Col, ast.SortOrder)] = ast.Col(wt1.value.name, wt2.value.name) -> ast.DESC :: next.columns
    }
}
