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

class SelectSpec extends FunSpec {

  import shapeless._
  import syntax.singleton._

  it("select") {

    val table = Select.tableOf[Row22].name("test")

    table.select('*.narrow)

    table.select('a.narrow, 'b.narrow, 'c.narrow)

    table.select('w.narrow)

    import shapeless.test._

    illTyped { """ table.select('invalid.narrow)""" }
  }
}
