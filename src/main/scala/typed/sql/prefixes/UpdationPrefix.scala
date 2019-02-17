package typed.sql.prefixes

import shapeless.{HList, ProductArgs, RecordArgs}
import typed.sql.internal.Repr2Ops.InferUpdateSet
import typed.sql._

class UpdationPrefix[A, Rs, Ru](table: Table[A, Rs, Ru]) {

  object set extends ProductArgs {
    def applyProduct[In <: HList, R <: HList](values: In)(
      implicit
      inferUpdateSet: InferUpdateSet.Aux[Ru, In, R]
    ): Update[From[Table[A, Rs, Ru]], R] = new Update[From[Table[A, Rs, Ru]], R] {
      override def astData: ast.Update = ast.Update(table.name, inferUpdateSet.mkAst, None)
      override def in: R = inferUpdateSet.out(values)
    }
  }

}
