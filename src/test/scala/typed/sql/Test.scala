package typed.sql

import org.scalatest.FunSpec

case class TestRow(
  a: Int,
  b: String,
  c: Boolean
)

class Test extends FunSpec {

  it("test dsl") {

    import typed.sql.syntax._

    val table = Table.of[TestRow].name("my_table")

    val a = table.col('a)
    val b = table.col('b)
    val c = table.col('c)

    val all = select(*) from table

    val aOnly = select(a) from table

    println(all.sql)
    println(aOnly.sql)

    val withWhere = {
      select(*)
        .from(table)
        .where((a ==== 1) and (b like "abc%"))
    }

    println(withWhere.sql)
  }
}
