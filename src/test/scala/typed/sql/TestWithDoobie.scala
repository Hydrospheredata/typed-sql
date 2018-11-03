package typed.sql

import cats.effect._
import doobie.util.transactor.Transactor
import org.scalatest.FunSpec
import doobie._
import doobie.implicits._
import doobie.syntax._
import cats.implicits._
import typed.sql.syntax._

import scala.concurrent.ExecutionContext

case class DTRow1(
  a: Int,
  b: String,
  c: String
)

case class DTRow2(
  f1: Int,
  f2: String,
  f3: String
)

case class DTRow3(
  x: Int,
  y: String,
  z: String
)

class TestWithDoobie extends FunSpec {

  val table1 = Table.of[DTRow1].primaryKey('a).name('test)
  val table2 = Table.of[DTRow2].name('yoyo)
  val table3 = Table.of[DTRow3].name('haha)

  val a1 = table1.col('a)
  val b1 = table1.col('b)
  val c1 = table1.col('c)

  val f1_2 = table2.col('f1)

  val x3 = table3.col('x)

  import typed.sql.toDoobie._

  it("select") {

    implicit def contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val xa = Transactor.fromDriverManager[IO](
      "org.h2.Driver",
      "jdbc:h2:mem:refined;DB_CLOSE_DELAY=-1",
      "sa", ""
    )

    val createSql1 = sql"CREATE TABLE test (a serial primary key, b text, c text)"
    createSql1.update.run.transact(xa).unsafeRunSync()
    val createSql2 = sql"CREATE TABLE yoyo (f1 serial primary key, f2 text, f3 text)"
    createSql2.update.run.transact(xa).unsafeRunSync()
    val createSql3 = sql"CREATE TABLE haha (x serial primary key, y text, z text)"
    createSql3.update.run.transact(xa).unsafeRunSync()

    (0 to 3).foreach(i => {
      val bv = "b" * i
      val cv = s"c_$i"
      val x = sql"INSERT INTO test (b, c) VALUES ($bv, $cv)"
      x.update.run.transact(xa).unsafeRunSync()
    })

    (0 to 3).foreach(i => {
      val bv = "b" * i
      val cv = s"c_$i"
      val x = sql"INSERT INTO yoyo (f2, f3) VALUES ($bv, $cv)"
      x.update.run.transact(xa).unsafeRunSync()
    })

    (0 to 2).foreach(i => {
      val bv = "b" * i
      val cv = s"c_$i"
      val x = sql"INSERT INTO haha (y, z) VALUES ($bv, $cv)"
      x.update.run.transact(xa).unsafeRunSync()
    })

    val sAll1 = select(*).from(table1)
    val res1: List[DTRow1] = sAll1.toQuery.to[List].transact(xa).unsafeRunSync()
    println(res1)


    val sAll2 = select(*).from(table1.innerJoin(table2).on(a1 <==> f1_2))
    val res2: List[(DTRow1, DTRow2)] = sAll2.toQuery.to[List].transact(xa).unsafeRunSync()
    println(res2)


    val sAll3 = select(*).from(table1.leftJoin(table2).on(a1 <==> f1_2))
    val res3: List[(DTRow1, Option[DTRow2])] = sAll3.toQuery.to[List].transact(xa).unsafeRunSync()
    println(res3)

    val sWhere1 = select(*).from(table1.leftJoin(table2).on(a1 <==> f1_2)).where(a1 > 2 and a1 < 5)
    val resW1: List[(DTRow1, Option[DTRow2])] = sWhere1.toQuery.to[List].transact(xa).unsafeRunSync()
    println(resW1)


    val sAll4 =
      select(*).from(
        table1
          .leftJoin(table2).on(a1 <==> f1_2)
          .leftJoin(table3).on(a1 <==> x3)
      )
    println(sAll4.toFragment)
    val res4 = sAll4.toQuery.to[List].transact(xa).unsafeRunSync()
    println(res4)


    val del1 = delete from table1 where a1 ==== 1
    val delR = del1.toUpdate.run.transact(xa).unsafeRunSync()
    println(delR)

    val upd1 = update(table1).set(b1 := "UPDATED B").where(a1 ==== 2)
    val updR = upd1.toUpdate.run.transact(xa).unsafeRunSync()
    println(updR)
  }
}
