package typed.sql.prefixes

import shapeless.{HList, ProductArgs, RecordArgs}
import typed.sql.internal.Repr2Ops.InferUpdateSet
import typed.sql._

class UpdationPrefix[A, N, Rs <: HList, Ru <: HList](table: TableUpd[A, N, Rs, Ru]) {

  object set extends ProductArgs {
    def applyProduct[In <: HList, R <: HList](values: In)(
      implicit
      inferUpdateSet: InferUpdateSet.Aux[Ru, In, R]
    ): Updation[R] = new Updation[R] {
      override def astData: ast.Update = ast.Update(table.name, inferUpdateSet.mkAst)
      override def in: R = inferUpdateSet.out(values)
    }
  }

}
