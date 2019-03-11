package typed.sql.prefixes

import shapeless.{HList, ProductArgs, RecordArgs}
import typed.sql._
import typed.sql.internal.UpdateAst

class UpdationPrefix[A, Rs, Ru](table: Table[A, Rs, Ru]) {

  object set extends ProductArgs {
    def applyProduct[In <: HList, R <: HList](values: In)(
      implicit
      inferUpdateSet: UpdateAst.Aux[Table[A, Rs, Ru], In, R]
    ): Update[Table[A, Rs, Ru], R] = new Update[Table[A, Rs, Ru], R] {
      override def astData: ast.Update = ast.Update(table.name, inferUpdateSet.mkAst(values), None)
      override def in: R = inferUpdateSet.out(values)
    }
  }

}
