package typed.sql

import shapeless._
import typed.sql.internal.WhereInfer
import typed.sql.prefixes._

object syntax extends ColumnSyntax {

  object select extends ProductArgs {
    def applyProduct[A](query: A): SelectionPrefix[A] = SelectionPrefix(query)
  }

  object delete {

    def from[A, N, Rs <: HList, Ru <: HList](table: Table[A, N, Rs, Ru]): Deletion[From[TRepr[A, N, Rs, Ru]], HNil] =
      Deletion.create(From(table.repr), table.name)

  }

  def update[A, N, Rs <: HList, Ru <: HList](table: Table[A, N, Rs, Ru]): UpdationPrefix[A, N, Rs, Ru] = new UpdationPrefix(table)

  val `*` = All

  implicit class WhereSelectSyntax[S <: FSH, O](selection: Selection[S, O, HNil]) {

    def where[C <: WhereClause, In0 <: HList](c: C)(implicit inf: WhereInfer.Aux[S, C, In0]): Selection[S, O, In0] =
      new Selection[S, O, In0] {
        type WhereFlag = Selection.WhereDefined.type
        def astData: ast.Select[O] = selection.astData.copy(where = Some(inf.mkAst(c)))
        def in: In0 = inf.out(c)
      }
  }

  implicit class WhereDeleteSyntax[S <: FSH, O](deletion: Deletion[S, O]) {

    def where[C <: WhereClause, In0 <: HList](c: C)(implicit inf: WhereInfer.Aux[S, C, In0]): Deletion[S, In0] =
      new Deletion[S, In0] {
        def astData: ast.Delete = deletion.astData.copy(where = Some(inf.mkAst(c)))
        def in: In0 = inf.out(c)
      }
  }

  implicit class JoinSyntax[A <: FSH](shape: A) {

    def innerJoin[S2, N2, Rs2 <: HList, ru <: HList](t: Table[S2, N2, Rs2, ru]): IJPrefix[A, S2, N2, Rs2, ru] =
      new IJPrefix(shape, From(t.repr))

    def leftJoin[S2, N2, Rs2 <: HList, ru <: HList](t: Table[S2, N2, Rs2, ru]): LJPrefix[A, S2, N2, Rs2, ru] =
      new LJPrefix(shape, From(t.repr))

    def rightJoin[S2, N2, Rs2 <: HList, ru <: HList](t: Table[S2, N2, Rs2, ru]): RJPrefix[A, S2, N2, Rs2, ru] =
      new RJPrefix(shape, From(t.repr))

    def fullJoin[S2, N2, Rs2 <: HList, ru <: HList](t: Table[S2, N2, Rs2, ru]): FJPrefix[A, S2, N2, Rs2, ru] =
      new FJPrefix(shape, From(t.repr))
  }

}
