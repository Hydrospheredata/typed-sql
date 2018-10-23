package croodie

import cats.effect.{Async, ContextShift, IO}
import org.scalatest.FunSpec
import shapeless._
import doobie._
import doobie.implicits._
import doobie.syntax._
import cats.implicits._

import scala.concurrent.{ExecutionContext, Future}

case class Row(
  a: Int,
  b: String,
  c: String
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




    val table = Select.tableOf[Row].name("test")
    val govnoOps = table.upsertOps('a)

    println("INSERT 0 to 10")
    val res = (0 to 10).map(i => {
      val r = Row(-1, i + "_b", "c" * i)
      govnoOps.insert(r).transact(xa).unsafeRunSync()
    })
    println(res.mkString("\n"))

    println("SELECT *")
    val selectStar = table.select('*.narrow)
    println(selectStar.query.to[List].transact(xa).unsafeRunSync().mkString("\n"))
    println()

    import cmp._

    println("SELECT WHERE a = 5")
    val selectWhere = table.select('*.narrow).where(eqOp('a)(5))
    val a5 = selectWhere.query.option.transact(xa).unsafeRunSync().get
    println("A5:" + a5)
    println()

    println("UPDATE where a = 5")
    val upd = a5.copy(b = "UPDATED")
    val updated = govnoOps.update(upd).transact(xa).unsafeRunSync()
    println()

    println("SELECT WHERE a = 5")
    val a5Upd = selectWhere.query.option.transact(xa).unsafeRunSync()
    println("A5:" + a5Upd)
    println()

    println("DELETE WHERE a = 5")
    val delete = table.delete.where(eqOp('a)(5))
    println(delete.update.run.transact(xa).unsafeRunSync())
    println()

    println("SELECT *")
    println(selectStar.query.to[List].transact(xa).unsafeRunSync().mkString("\n"))
    println()
  }
}
