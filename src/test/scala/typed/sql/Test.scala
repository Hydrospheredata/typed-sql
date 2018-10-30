package typed.sql

import org.scalatest.FunSpec
import shapeless.test.illTyped
import typed.sql.internal.FSHOps.{CanJoin, IsFieldOf}

case class TestRow(
  a: Int,
  b: String,
  c: Boolean
)

case class Row2(
  first: Int,
  second: String,
  third: Boolean,
  fourth: Long
)

case class Row3(
  x: Int,
  y: String
)

class Test extends FunSpec {

  import typed.sql.syntax._

  val table1 = Table3.of[TestRow].name('my_table)
  val table2 = Table3.of[Row2].name('second_table)
  val table3 = Table3.of[Row3].name('third)

  val a1 = table1.col('a)
  val b1 = table1.col('b)
  val c1 = table1.col('c)

  val first2 = table2.col('first)
  val second2 = table2.col('second)

  val x3 = table3.col('x)

  val joined1 = table1 innerJoin table2 on a1 <==> first2

  val joined2 = table1
    .innerJoin(table2).on(a1 <==> first2)
    .innerJoin(table3).on(a1 <==> x3)

  it("select *") {
    val x1 = select(*).from(table1)
    println(x1.astData)

    val x2 = select(*).from(joined1)
    println(x2.astData)

    val x3 = select(*).from(table1.fullJoin(table2).on(a1 <==> first2))
    println(x3.astData)
  }

  it("select fields") {
    val x1 = select(a1).from(table1)
    println(x1.astData)

    val x2 = select(b1).from(table1)
    println(x2.astData)

    val x3 = select(c1).from(table1)
    println(x3.astData)

    val x4 = select(a1, c1).from(table1)
    println(x4.astData)

    val x5 = select(a1, second2, c1).from(table1 leftJoin table2 on a1 <==> first2)
    println(x5.astData)
  }

  it("joins") {
    illTyped{"table1.innerJoin(table2).on(a1 <==> x3)"}
  }

}
