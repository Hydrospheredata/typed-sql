package typed.sql.prefixes

import shapeless.HList
import typed.sql._
import typed.sql.internal.SelectInfer

case class SelectionPrefix[Q](query: Q) {

  def from[S <: FSH, O](shape: S)(
    implicit
    inf: SelectInfer.Aux[S, Q, O]
  ): Selection[S, O] = Selection.create(inf.mkAst(shape))

  def from[A, N, R <: HList, O](table: Table3[A, N, R])(
    implicit
    inf: SelectInfer.Aux[From[TRepr[A, N, R]], Q, O]
  ): Selection[From[TRepr[A, N, R]], O] = Selection.create(inf.mkAst(table.shape))

}
