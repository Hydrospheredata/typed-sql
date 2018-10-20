package croodie

import org.scalatest.FunSpec
import shapeless._
import doobie._
import doobie.implicits._
import doobie.syntax._
import shapeless.labelled.FieldType

case class Row(
  a: Int,
  b: String,
  c: String,
  xxxx: Long
)

class Test extends FunSpec {

  import shapeless._
  import syntax.singleton._

  it("asddas") {

    val z = 'z.narrow
    val y = 'y.narrow
    val w = 'w.narrow
    val table = Select.tableOf[Row].name("my_table")
    val sle = table.select('a.narrow)
    println(sle.fr)
  }
}
