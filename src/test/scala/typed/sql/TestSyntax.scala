package typed.sql

import org.scalatest.{FunSpec, Matchers}
import shapeless.test.illTyped
import typed.sql.syntax._

class TestSyntax extends FunSpec with Matchers{

  case class TestRow(
    a: Int,
    b: String,
    c: Boolean
  )
  val table1 = Table.of[TestRow].name('my_table)
  val a1 = table1.col('a)

  describe("delete") {

    it("delete") {
      delete.from(table1).astData shouldBe ast.Delete("my_table", None)
    }

    it("delete where") {
      val exp = ast.Delete("my_table", Some(ast.WhereEq(ast.Col("my_table", "a"))))
      delete.from(table1).where(a1 ==== 2).astData shouldBe exp
    }
  }

  describe("update") {

    val testUpdate = Table.of[TestRow].primaryKey('a).name('test_upd)

    val a1 = testUpdate.col('a)
    val b1 = testUpdate.col('b)

    it("update column") {
      val x = update(testUpdate).set(b1 := "yoyo")
      x.astData shouldBe ast.Update("test_upd", List(ast.Set(ast.Col("test_upd", "b"))))
    }

    it("can't update primary key") {
      illTyped{"update(testUpdate).set(a1 := 42)"}
    }
  }
}
