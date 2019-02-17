package typed.sql.prefixes

import shapeless._
import shapeless.ops.record.Values
import typed.sql.{Insert, Table, ast}
import typed.sql.internal.InsertValuesInfer

class InsertIntoPrefix[A, Rs, Ru](table: Table[A, Rs, Ru]) {

  def values[In, In2 <: HList](vs: In)(
    implicit
    gen: Generic.Aux[In, In2],
    inferInsertValues: InsertValuesInfer[Ru, In2]
  ): Insert[In2] = {
    val n = table.name
    val cols = inferInsertValues.columns.map(v => ast.Col(n, v))
    val in = gen.to(vs)
    Insert(ast.InsertInto(n, cols), in)
  }

}

