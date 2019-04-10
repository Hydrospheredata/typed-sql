package typed.sql

import shapeless.{Generic, HList, HNil, ProductArgs}
import typed.sql.internal.{InsertValuesInfer, SelectInfer, UpdateAst}

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
  
  class SetWord[A, Rs, Ru](table: Table[A, Rs, Ru]) {
    
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
  
  def update[A, Rs, Ru](table: Table[A, Rs, Ru]): SetWord[A, Rs, Ru] = new SetWord(table)
}

trait InsertInto[T, In] extends SQLQuery[ast.InsertInto, In]
trait InsertIntoSyntax {
  
  class ValuesWord[A, Rs, Ru](table: Table[A, Rs, Ru]) {
  
    def values[In, In2 <: HList](vs: In)(
      implicit
      gen: Generic.Aux[In, In2],
      inferInsertValues: InsertValuesInfer[Ru, In2]
    ): InsertInto[Table[A, Rs, Ru], In2] = {
      
      val n = table.name
      val cols = inferInsertValues.columns.map(v => ast.Col(n, v))
      val in = gen.to(vs)
      
      new InsertInto[Table[A, Rs, Ru], In2] {
        override def astData: ast.InsertInto = ast.InsertInto(table.name, cols)
        override def params: In2 = in
      }
    }
    
  }
  
  object insert {
    def into[A, Rs <: HList, Ru <: HList](table: Table[A, Rs, Ru]): ValuesWord[A, Rs, Ru] = new ValuesWord(table)
  }
}

trait Select[S, In, Out, WF] extends SQLQuery[ast.Select[Out], In]
trait SelectSyntax {
  
  class FromWord[Q](query: Q) {
    
    def from[S, O](shape: S)(
      implicit
      inf: SelectInfer.Aux[S, Q, O]
    ): Select[S, HNil, O, WhereFlag.NotUsed] = {
      new Select[S, HNil, O, WhereFlag.NotUsed] {
        override def astData: ast.Select[O] = inf.mkAst(shape, query)
        override def params: HNil = HNil
      }
    }
    
  }
  
  object select extends ProductArgs {
    def applyProduct[Q](query: Q): FromWord[Q] = new FromWord(query)
  }
  
  val `*`  = All
}
object SelectSyntax extends SelectSyntax

