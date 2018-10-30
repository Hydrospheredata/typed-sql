package typed.sql

import shapeless._
import typed.sql.internal.SelectInfer2

case class SelectionPrefix[Q](query: Q) {

  def from[S <: FSH, O](shape: S)(
    implicit
    inf: SelectInfer2.Aux[S, Q, O]
  ): ast.Select[O] = inf.mkAst(shape)

  def from[A, N, R <: HList, O](table: Table3[A, N, R])(
    implicit
    inf: SelectInfer2.Aux[From[TRepr[A, N, R]], Q, O]
  ): ast.Select[O] = inf.mkAst(table.shape)

}

