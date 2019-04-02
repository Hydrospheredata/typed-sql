package typed.sql

import shapeless.{HList, HNil, ProductArgs}
import typed.sql.internal.UpdateAst

sealed trait WhereFlag
object WhereFlag {
  sealed trait Used extends WhereFlag
  sealed trait NotUsed extends WhereFlag
}

trait SQLQuery[AST <: ast.QueryAST, In] {
  def astData: AST
  def params: In
}

trait Delete[T, In, WF] extends SQLQuery[ast.Delete, In]

trait DeleteSyntax {
  
  object delete {
    def from[A, Rs, Ru](table: Table[A, Rs, Ru]): Delete[Table[A, Rs, Ru], HNil, WhereFlag.NotUsed] =
      new Delete[Table[A, Rs, Ru], HNil, WhereFlag.NotUsed] {
        def astData: ast.Delete = ast.Delete(table.name, None)
        def params: HNil = HNil
      }
  }
}

trait Update[T, In, WF] extends SQLQuery[ast.Update, In]

trait UpdateSyntax {
  
  class UpdateSetWord[A, Rs, Ru](table: Table[A, Rs, Ru]) {
    
    object set extends ProductArgs {
      def applyProduct[In <: HList, R <: HList](values: In)(
        implicit
        inferUpdateSet: UpdateAst.Aux[Table[A, Rs, Ru], In, R]
      ): Update[Table[A, Rs, Ru], R, WhereFlag.NotUsed] = new Update[Table[A, Rs, Ru], R, WhereFlag.NotUsed] {
        override def astData: ast.Update = ast.Update(table.name, inferUpdateSet.mkAst(values), None)
        override def params: R = inferUpdateSet.out(values)
      }
    }
    
  }
  
  def update[A, Rs, Ru](table: Table[A, Rs, Ru]): UpdateSetWord[A, Rs, Ru] = new UpdateSetWord(table)
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

