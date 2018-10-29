package typed.sql.prefixes

import typed.sql.internal.ShapeJoin
import typed.sql.{JoinCond, SHead, TableRepr}

class InnerJoinPrefix[A <: SHead, B <: TableRepr[_, _, _]](a: A, b: B) {

  def on[C <: JoinCond, O <: SHead](c: C)(
    implicit
    sj: ShapeJoin.Aux[A, B, C, O]
  ): O  = sj.innerJoin(a, b, c)

}
