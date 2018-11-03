package typed.sql.prefixes

import shapeless.{HList, ProductArgs, RecordArgs}
import typed.sql.internal.Repr2Ops.InferUpdateSet
import typed.sql._

class UpdationPrefix[A, N, Rs <: HList, Ru <: HList](table: Table[A, N, Rs, Ru]) {

  object set extends ProductArgs {
    def applyProduct[In <: HList, R <: HList](values: In)(
      implicit
      inferUpdateSet: InferUpdateSet.Aux[Ru, In, R]
    ): Updation[From[TRepr[A, N, Rs, Ru]], R] = new Updation[From[TRepr[A, N, Rs, Ru]], R] {
      override def astData: ast.Update = ast.Update(table.name, inferUpdateSet.mkAst, None)
      override def in: R = inferUpdateSet.out(values)
    }
  }

}
