package croodie

import shapeless.Witness

case class Column[K](v: K)

object columns {

  def col(k: Witness): Column[k.T] = Column(k.value)

}
