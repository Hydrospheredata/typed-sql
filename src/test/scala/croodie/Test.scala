package croodie

import cats.effect.{ContextShift, IO}
import org.scalatest.FunSpec
import shapeless._
import doobie._
import doobie.implicits._
import doobie.syntax._
import shapeless.labelled.FieldType

import scala.concurrent.ExecutionContext

case class Row(
  a: Int,
  b: String,
  c: String
)

class Test extends FunSpec {

  import shapeless._
  import syntax.singleton._

  implicit def contextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  val xa = Transactor.fromDriverManager[IO](
    "org.h2.Driver",
    "jdbc:h2:mem:refined;DB_CLOSE_DELAY=-1",
    "sa", ""
  )


  it("asddas") {

    val creareSql = sql"create table test (a serial primary key, b text, c text)"
    creareSql.update.run.transact(xa).unsafeRunSync()

    val bbb = "bbb"
    val ccc = "ccc"
    val insertSql = sql"insert into test (b, c) values ($bbb, $ccc)"
    insertSql.update.run.transact(xa).unsafeRunSync()


    val table = Select.tableOf[Row].name("test")

    val selectStar = table.select('*.narrow)
    println(selectStar.query.to[List].transact(xa).unsafeRunSync())
    val selectFields = table.select('a.narrow, 'b.narrow)
    println(selectFields.query.to[List].transact(xa).unsafeRunSync())
//    println(sle.fr)
  }
}
