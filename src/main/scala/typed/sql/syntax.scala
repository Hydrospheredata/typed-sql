package typed.sql

import shapeless._
import typed.sql.internal.WhereInfer
import typed.sql.prefixes._

object syntax extends ColumnSyntax {

  object select extends ProductArgs {
    def applyProduct[A](query: A): SelectionPrefix[A] = SelectionPrefix(query)
  }

  val `*` = All

  implicit class WhereSyntax[S <: FSH, O](selection: Selection[S, O, HNil]) {

    def where[C <: WhereClause, In0 <: HList](c: C)(implicit inf: WhereInfer.Aux[S, C, In0]): Selection[S, O, In0] =
      new Selection[S, O, In0] {
        type WhereFlag = Selection.WhereDefined.type
        def astData: ast.Select[O] = selection.astData.copy(where = Some(inf.mkAst(c)))
        def in: In0 = inf.out(c)
      }
  }

  implicit class JoinSyntax[A <: FSH](shape: A) {

    def innerJoin[S2, N2, R2 <: HList](t: Table[S2, N2, R2]): IJPrefix[A, S2, N2, R2] =
      new IJPrefix(shape, t.shape)

    def leftJoin[S2, N2, R2 <: HList](t: Table[S2, N2, R2]): LJPrefix[A, S2, N2, R2] =
      new LJPrefix(shape, t.shape)

    def rightJoin[S2, N2, R2 <: HList](t: Table[S2, N2, R2]): RJPrefix[A, S2, N2, R2] =
      new RJPrefix(shape, t.shape)

    def fullJoin[S2, N2, R2 <: HList](t: Table[S2, N2, R2]): FJPrefix[A, S2, N2, R2] =
      new FJPrefix(shape, t.shape)
  }

}
