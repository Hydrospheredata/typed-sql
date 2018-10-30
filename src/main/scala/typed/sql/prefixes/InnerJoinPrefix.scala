package typed.sql.prefixes

import typed.sql.internal.InnerJoiner
import typed.sql.{JoinCond, SHead, TableRepr}

class InnerJoinPrefix[A <: SHead, B <: TableRepr[_, _, _]](a: A, b: B) {

  def on[C <: JoinCond, O <: SHead](c: C)(
    implicit
    sj: InnerJoiner.Aux[A, B, C, O]
  ): O  = sj.innerJoin(a, b, c)

}
