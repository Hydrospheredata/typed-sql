package typed.sql.prefixes

import shapeless._
import shapeless.ops.record.Values
import typed.sql.{Insert, Table, ast}
import typed.sql.internal.InsertValuesInfer

class InsertIntoPrefix[A, N, Rs <: HList, Ru <: HList](table: Table[A, N, Rs, Ru]) {

  object values extends ProductArgs {

    def applyProduct[In <: HList](vs: In)(
      implicit
      inferInsertValues: InsertValuesInfer[Ru, In]
    ): Insert[In] = {
      //TODO columns need table only in selects
      val n = table.name
      val cols = inferInsertValues.columns.map(v => ast.Col(n, v))
      val in = vs
      Insert(ast.InsertInto(n, cols), in)
    }

  }

}

