package typed.sql

import shapeless.HNil
import typed.sql.internal.WhereAst

sealed trait WhereFlag
object WhereFlag {
  
  sealed trait WhereUsed extends WhereFlag
  sealed trait WhereNotUsed extends WhereFlag
  
  case object WhereUsed extends WhereUsed
  case object WhereNotUsed extends WhereNotUsed
  
}

trait SQLQuery[AST <: ast.QueryAST, In] {
  def astData: AST
  def params: In
}

trait Delete[T, In, WF] extends SQLQuery[ast.Delete, In] { self =>
  
  def where[C, In0](c: C)(implicit
    ev: WF <:< WhereFlag.WhereNotUsed,
    inf: WhereAst.Aux[T, C, In0],
  ): Delete[T, In0, WhereFlag.WhereUsed] =
    new Delete[T, In0, WhereFlag.WhereUsed] {
      def astData: ast.Delete = self.astData.copy(where = Some(inf.mkAst(c)))
      def params: In0 = inf.params(c)
    }
  
}

trait DeleteSyntax {
  
  object delete {
    def from[A, Rs, Ru](table: Table[A, Rs, Ru]): Delete[Table[A, Rs, Ru], HNil, WhereFlag.WhereNotUsed] =
      new Delete[Table[A, Rs, Ru], HNil, WhereFlag.WhereNotUsed] {
        def astData: ast.Delete = ast.Delete(table.name, None)
        def params: HNil = HNil
      }
  }
}



case class Insert[In](astData: ast.InsertInto, in: In)

trait Select[S <: FSH, Out, In] {
  type WhereFlag <: Select.HasWhere

  def astData: ast.Select[Out]
  def in: In
}

object Select {

  sealed trait HasWhere
  case object WhereDefined extends HasWhere
  case object WithoutWhere extends HasWhere

  def create[S <: FSH, Out](select: ast.Select[Out]): Select[S, Out, HNil] = {
    new Select[S, Out , HNil] {
      type WhereFlag = WithoutWhere.type
      val astData: ast.Select[Out] = select
      val in: HNil = HNil
    }
  }
}

trait Update[S, In] {
  def astData: ast.Update
  def in: In
}
