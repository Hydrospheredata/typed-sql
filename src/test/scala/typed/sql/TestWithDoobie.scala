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
  f2: String,
  f1: Int,
  f3: String
)

class TestWithDoobie extends FunSpec {

  val table1 = Table3.of[DTRow1].name('test)
  val table2 = Table3.of[DTRow2].name('yoyo)

  import typed.sql.toDoobie._

  it("select") {

    implicit def contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val xa = Transactor.fromDriverManager[IO](
      "org.h2.Driver",
      "jdbc:h2:mem:refined;DB_CLOSE_DELAY=-1",
      "sa", ""
    )

    val createSql = sql"CREATE TABLE test (a serial primary key, b text, c text)"
    createSql.update.run.transact(xa).unsafeRunSync()

    (0 to 10).foreach(i => {
      val bv = "b" * i
      val cv = s"c_$i"
      val x = sql"INSERT INTO test (b, c) VALUES ($bv, $cv)"
      x.update.run.transact(xa).unsafeRunSync()
    })

    val sAll = select(*).from(table1)
    val q = sAll.toQuery

    val res = q.to[List].transact(xa).unsafeRunSync()
    println(res)
  }
}
