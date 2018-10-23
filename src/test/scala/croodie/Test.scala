package croodie

import cats.effect.{Async, ContextShift, IO}
import croodie.FieldSelector.GetField
import org.scalatest.FunSpec
import shapeless._
import doobie._
import doobie.implicits._
import doobie.syntax._
import shapeless.labelled.FieldType

import scala.concurrent.{ExecutionContext, Future}

case class Row(
  c: String,
  a: Int,
  b: String
)

class Test extends FunSpec {

  import shapeless._
  import syntax.singleton._

  it("select") {

    implicit def contextShift: ContextShift[IO] =
      IO.contextShift(ExecutionContext.global)

    val xa = Transactor.fromDriverManager[IO](
      "org.h2.Driver",
      "jdbc:h2:mem:refined;DB_CLOSE_DELAY=-1",
      "sa", ""
    )

    val creareSql = sql"create table test (a serial primary key, b text, c text)"
    creareSql.update.run.transact(xa).unsafeRunSync()

    val bbb = "bbb"
    val ccc = "ccc"
    val bbbb = "bbbb"
    val cccc = "cccc"
    val insertSql = sql"insert into test (b, c) values ($bbb, $ccc)"
    insertSql.update.run.transact(xa).unsafeRunSync()
    val insertSql2 = sql"insert into test (b, c) values ($bbbb, $cccc)"
    insertSql2.update.run.transact(xa).unsafeRunSync()


    val table = Select.tableOf[Row].name("test")

    val selectStar = table.select('*.narrow)

    println(selectStar.query.to[List].transact(xa).unsafeRunSync())
    val selectFields = table.select('b.narrow)
    println(selectFields.query.to[List].transact(xa).unsafeRunSync())

    import cmp._

    val selectWhere = table.select('*.narrow).where(And(eqOp('b)("bbb"), eqOp('a)(1)))
    // fd('a) == 1 and fd('b)
//    val selectWhere = table.select('*.narrow).where(eqOp('a)(1))
    println(selectWhere.query.to[List].transact(xa).unsafeRunSync())
  }
//
  it("join") {

    val aF = 'a.narrow
    val bF = 'b.narrow
    val cF = 'c.narrow
    val dF = 'd.narrow

    val x = 'b.witness

    type shape = FieldType[aF.type, Int] :: FieldType[bF.type, String] :: FieldType[cF.type, String] :: HNil

    val z = FieldSelector[FieldType[aF.type, Int] :: FieldType[bF.type, String] :: FieldType[cF.type, String] :: HNil, cF.type :: HNil]


  }
//
//  it("where") {
//
//    import cmp._
//
//    val e = eqOp('a)(10)
//
//    val aF = 'a.narrow
//    val bF = 'b.narrow
//
////    implicitly[GetField[FieldType[aF., Int] :: HNil, bF.type]]
//    val z = WhereUnitInfer[FieldType[aF.type, String] :: FieldType[bF.type, String]:: HNil, And[Eq[bF.type, String], Eq[bF.type, String]]].apply().sql
//    println(z)
//  }

//  it("get field") {
//
//    val aF = 'a.narrow
//    val bF = 'b.narrow
//    val cF = 'c.narrow
//    val dF = 'd.narrow
//    val xF = 'x.narrow
//    GetField[FieldType[aF.type, Int] :: FieldType[bF.type, String] :: FieldType[cF.type, String] :: FieldType[dF.type, String] :: HNil, xF.type]
//    //GetField[FieldType[aF.type, String] :: FieldType[bF.type, String]:: FieldType[cF.type, Int] :: HNil, cF.type]
//  }
}
