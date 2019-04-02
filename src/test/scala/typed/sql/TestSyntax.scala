package typed.sql

import cats.data.NonEmptyList
import org.scalatest.{FunSpec, Matchers}
import shapeless._
import shapeless.test.illTyped
import typed.sql.syntax._

class TestSyntax extends FunSpec with Matchers{

  case class TestRow(
    a: Int,
    b: String,
    c: Boolean
  )
  val table1 = Table.of[TestRow].autoColumn('a).name("my_table")
  val a1 = table1.col('a)
  val b1 = table1.col('b)
  
  case class XXX(a: Int)
  val table2 = Table.of[XXX].name("xxx")
  val `x.a1` = table2.col('a)

  describe("delete") {

    it("delete") {
      delete.from(table1).astData shouldBe ast.Delete("my_table", None)
    }

    it("delete where") {
      val exp = ast.Delete("my_table", Some(ast.WhereEq(ast.Col("my_table", "a"))))
      val x = delete.from(table1)
      delete.from(table1).where(a1 === 2).astData shouldBe exp
    }
    it("delete double where ill") {
      illTyped("""
        val exp = ast.Delete("my_table", Some(ast.WhereEq(ast.Col("my_table", "a"))))
        delete.from(table1).where(a1 === 2).where(a1 === 3).astData shouldBe exp
      """)
    }
    
    it("delete where ill") {
      illTyped("""
        val exp = ast.Delete("my_table", Some(ast.WhereEq(ast.Col("my_table", "a"))))
        delete.from(table1).where(`x.a1` === 2).astData shouldBe exp
      """)
    }
  }

  describe("update") {

    it("update column") {
      val x = update(table1).set(b1 := "yoyo")
      x.astData shouldBe ast.Update("my_table", List(ast.Set(ast.Col("my_table", "b"))), None)
    }

    it("can't update primary key") {
      illTyped{"update(table1).set(a1 := 42)"}
    }

    it("with where") {
      val x = update(table1).set(b1 := "yoyo").where(a1 === 4)
      x.astData shouldBe ast.Update("my_table", List(ast.Set(ast.Col("my_table", "b"))), Some(ast.WhereEq(ast.Col("my_table", "a"))))
    }
  }
//
//  describe("insert into") {
//
//    it("insert all") {
//      val x = insert.into(table1).values("b_value", "c_value")
//      val data = x.astData
//
//      val exp = ast.InsertInto("my_table", List(ast.Col("my_table", "b"), ast.Col("my_table", "c")))
//      data shouldBe exp
//
//      x.in shouldBe ("b_value" :: "c_value" :: HNil)
//    }
//  }
//
//  describe("where") {
//
//    it("eq") {
//      val x = select(a1).from(table1).where(a1 === 2)
//      x.astData.where.get shouldBe ast.WhereEq(ast.Col("my_table", "a"))
//    }
//
//    it("gt") {
//      val x = select(a1).from(table1).where(a1 > 2)
//      x.astData.where.get shouldBe ast.Gt(ast.Col("my_table", "a"))
//    }
//
//    it("gt or eq") {
//      val x = select(a1).from(table1).where(a1 >= 2)
//      x.astData.where.get shouldBe ast.GtOrEq(ast.Col("my_table", "a"))
//    }
//
//    it("less") {
//      val x = select(a1).from(table1).where(a1 < 2)
//      x.astData.where.get shouldBe ast.Less(ast.Col("my_table", "a"))
//    }
//
//    it("less or eq") {
//      val x = select(a1).from(table1).where(a1 =< 2)
//      x.astData.where.get shouldBe ast.LessOrEq(ast.Col("my_table", "a"))
//    }
//
//    it("like") {
//      val x = select(a1).from(table1).where(b1 like "BBB%")
//      x.astData.where.get shouldBe ast.Like(ast.Col("my_table", "b"))
//    }
//
//    it("and") {
//      val x = select(a1).from(table1).where(a1 === 2 and a1 === 3)
//      x.astData.where.get shouldBe ast.And(ast.WhereEq(ast.Col("my_table", "a")), ast.WhereEq(ast.Col("my_table", "a")))
//    }
//
//    it("or") {
//      val x = select(a1).from(table1).where(a1 === 2 or a1 === 3)
//      x.astData.where.get shouldBe ast.Or(ast.WhereEq(ast.Col("my_table", "a")), ast.WhereEq(ast.Col("my_table", "a")))
//    }
//
//    it("and and") {
//      val x = select(a1).from(table1).where(a1 === 2 and a1 === 3 and a1 === 4)
//      x.astData.where.get shouldBe
//        ast.And(
//          ast.And(
//            ast.WhereEq(ast.Col("my_table", "a")),
//            ast.WhereEq(ast.Col("my_table", "a"))
//          ),
//          ast.WhereEq(ast.Col("my_table", "a"))
//        )
//    }
//
//    it("in") {
//      val x = select(a1).from(table1).where(a1 in NonEmptyList.of(1, 2, 3))
//      x.astData.where.get shouldBe ast.In(ast.Col("my_table", "a"), 3)
//    }
//  }
//
//  describe("select") {
//
//
//    describe("order by ") {
//      it("order by default") {
//        val x = select(a1).from(table1).orderBy(b1)
//        val data = x.astData
//
//        val exp = ast.Select(
//          List(ast.Col("my_table", "a")),
//          ast.From("my_table", List.empty),
//          None,
//          Some(ast.OrderBy(List(ast.Col("my_table", "b") -> ast.ASC))),
//          None, None
//        )
//        data shouldBe exp
//      }
//
//      it("order by asc") {
//        val x = select(a1).from(table1).orderBy(b1.ASC)
//        val data = x.astData
//
//        val exp = ast.Select(
//          List(ast.Col("my_table", "a")),
//          ast.From("my_table", List.empty),
//          None,
//          Some(ast.OrderBy(List(ast.Col("my_table", "b") -> ast.ASC))),
//          None, None
//        )
//        data shouldBe exp
//      }
//
//      it("order by desc") {
//        val x = select(a1).from(table1).orderBy(b1.DESC)
//        val data = x.astData
//
//        val exp = ast.Select(
//          List(ast.Col("my_table", "a")),
//          ast.From("my_table", List.empty),
//          None,
//          Some(ast.OrderBy(List(ast.Col("my_table", "b") -> ast.DESC))),
//          None, None
//        )
//        data shouldBe exp
//      }
//
//      it("order by mult") {
//        val x = select(a1).from(table1).orderBy(a1, b1)
//        val data = x.astData
//
//        val exp = ast.Select(
//          List(ast.Col("my_table", "a")),
//          ast.From("my_table", List.empty),
//          None,
//          Some(ast.OrderBy(
//            List(
//              ast.Col("my_table", "a") -> ast.ASC,
//              ast.Col("my_table", "b") -> ast.ASC
//            )
//          )),
//          None, None
//        )
//        data shouldBe exp
//      }
//    }
//  }
}
