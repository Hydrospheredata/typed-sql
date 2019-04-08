package typed.sql.internal

import shapeless._
import typed.sql.{Column, ast}

trait SelectFieldsInfer[A, Q] {
  type Out
  def mkAst(q: Q): List[ast.Col]
}

object SelectFieldsInfer {
  type Aux[A, Q, Out0] = SelectFieldsInfer[A, Q] { type Out = Out0 }
  
  implicit def last[A, N, V, T](
    implicit
    s2s: Symbol2Str[N]
  ): Aux[A, Column[N, V, T] :: HNil, V :: HNil] = {
    new SelectFieldsInfer[A, Column[N, V, T] :: HNil] {
      type Out = V :: HNil
      def mkAst(a: Column[N, V, T] :: HNil): List[ast.Col] = {
        val table = a.head.tableName
        List(ast.Col(table, s2s.str))
      }
    }
  }
  
  implicit def hlist[A, K, V, T, X <: HList, Out0 <: HList](
    implicit
    next: SelectFieldsInfer.Aux[A, X, Out0],
    s2s: Symbol2Str[K]
  ): Aux[A, Column[K, V, T] :: X, V :: Out0] = {
    new SelectFieldsInfer[A, Column[K, V, T] :: X] {
      type Out = V :: Out0
      def mkAst(a: Column[K, V, T] :: X): List[ast.Col] = {
        val table = a.head.tableName
        ast.Col(table, s2s.str) :: next.mkAst(a.tail)
      }
    }
  }


}
