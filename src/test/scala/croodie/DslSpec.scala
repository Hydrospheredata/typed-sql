package croodie

import org.scalatest.FunSpec

import cats.effect.{Async, ContextShift, IO}
import croodie.FieldSelector.GetField
import org.scalatest.FunSpec
import shapeless._
import doobie._
import doobie.implicits._
import doobie.syntax._
import shapeless.labelled.FieldType

import scala.concurrent.{ExecutionContext, Future}


case class Row22(
  a: Int,
  b: String,
  c: Int,
  d: Float,
  e: Long,
  f: Boolean,
  g: String,
  h: String,
  i: String,
  j: String,
  k: String,
  l: String,
  m: String,
  n: String,
  o: String,
  p: String,
  q: String,
  r: String,
  s: String,
  t: String,
  u: String,
  v: String,
  w: String
)

class DslSpec extends FunSpec {

  import shapeless._
  import syntax.singleton._

  import shapeless.test._
  import cmp._

  val table = Select.tableOf[Row22].name("test")

  it("select") {

    table.select('*.narrow)

    table.select('a.narrow, 'b.narrow, 'c.narrow)

    table.select('w.narrow)

    illTyped { """ table.select('invalid.narrow)""" }
  }

  it("where") {

    table.select('*.narrow).where(eqOp('a)(1))
    table.select('*.narrow).where(lessOp('a)(1))
    table.select('*.narrow).where(greaterOp('a)(1))

    illTyped { """table.select('*.narrow).where(eqOp('a)("zzzz")) """}

    table.select('*.narrow).where(Or(eqOp('a)(1), eqOp('a)(2)))
    table.select('*.narrow).where(And(greaterOp('a)(10), lessOp('a)(20)))

    table.select('*.narrow).where(And(greaterOp('a)(10), eqOp('w)("WWW")))
  }

  it("delete") {
    table.delete.where(eqOp('a)(1))
  }
}
