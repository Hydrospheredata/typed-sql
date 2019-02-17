package typed.sql.internal

import shapeless.Witness

trait Symbol2Str[S] {
  def str: String
}

object Symbol2Str {
  
  implicit def symbol2Str[S](implicit wt1: Witness.Aux[S], ev1: S <:< Symbol): Symbol2Str[S] =
    new Symbol2Str[S] {
      override def str: String = wt1.value.name
    }
}
