package typed.sql

import cats.data.NonEmptyList
import cats.effect._
import doobie.util.transactor.Transactor
import org.scalatest.FunSpec
import doobie._
import doobie.implicits._
import doobie.syntax._
import cats.implicits._
import doobie.scalatest.IOChecker

import typed.sql.syntax._
import typed.sql.toDoobie._

import scala.concurrent.ExecutionContext

case class BRow(
  a: Int,
  b: String,
  c: String
)
case class Row2(a2: Int)

//class H2Test extends FunSpec with IOChecker {
//
//  implicit def contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
//
//  val transactor = Transactor.fromDriverManager[IO](
//    "org.h2.Driver",
//    "jdbc:h2:mem:refined;DB_CLOSE_DELAY=-1",
//    "sa", ""
//  )
//
//
//  val table = Table.of[BRow].autoColumn('a).name('base)
//  val a = table.col('a)
//  val b = table.col('b)
//  val c = table.col('c)
//
//  val table2 = Table.of[Row2].name('test2)
//  val a2 = table2.col('a2)
//
//  val create1 = sql"CREATE TABLE base (a serial primary key, b varchar not null, c varchar not null)"
//  create1.update.run.transact(transactor).unsafeRunSync()
//
//  val create2 = sql"CREATE TABLE test2 (a2 serial primary key)"
//  create2.update.run.transact(transactor).unsafeRunSync()
//
//  describe("base") {
//    it("insert") { check { insert.into(table).values("bv", "cv").toUpdate }}
//    it("select *") { check { select(*).from(table).toQuery } }
//    it("select a, b") { check { select(a, b).from(table).toQuery }}
//    it("update b where a = 1") { check { update(table).set(b := "Upd B").where(a === 1).toUpdate }}
//  }
//
//  describe("where") {
//    it("gt and less") { check { select(*).from(table).where(a > 1 and a < 5).toQuery }}
//    it("geteq and lesseq") { check{ select(*).from(table).where(a >= 1 and a =< 5).toQuery }}
//    it("eq or") { check{ select(*).from(table).where(a === 1 or a === 2).toQuery }}
//    it("like") { check{ select(*).from(table).where(b like "BBB%").toQuery }}
//    it("in") { check{ select(*).from(table).where(a.in(NonEmptyList.of(1,2,3))).toQuery }}
//  }
//
//  describe("order by") {
//
//    it("a") { check { select(*).from(table).orderBy(a).toQuery }}
//    it("a desc") { check { select(*).from(table).orderBy(a.DESC).toQuery }}
//    it("a, b asc") {check { select(*).from(table).orderBy(a, b).toQuery }}
//  }
//
//// TODO: param meta returns VARCHAR type for limit and offset
////  it("limit/offset") {
////    check{
////      select(*).from(table).limit(10).offset(1).toQuery
////    }
////  }
//
//  describe("joins") {
//
//    it("ij") { check{ select(*).from(table.innerJoin(table2).on(a <==> a2)).toQuery }}
//    it("lj") { check{ select(*).from(table.leftJoin(table2).on(a <==> a2)).toQuery }}
//    it("rj") { check{ select(*).from(table.rightJoin(table2).on(a <==> a2)).toQuery }}
//    it("fj") { check{ select(*).from(table.fullJoin(table2).on(a <==> a2)).toQuery }}
//    it("a1+a2") { check{ select(a, a2).from(table.innerJoin(table2).on(a <==> a2)).toQuery }}
//  }
//}
