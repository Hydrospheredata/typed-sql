package typed.sql

/**
  * FROM Table shape.
  * Describes plain tables|joins
  */
sealed trait FSH
final case class From[A](table: A) extends FSH
sealed trait FSHJ extends FSH

final case class IJ[H, Cond, T](h: H, cond: Cond, tail: T) extends FSHJ
final case class LJ[H, Cond, T](h: H, cond: Cond, tail: T) extends FSHJ
final case class RJ[H, Cond, T](h: H, cond: Cond, tail: T) extends FSHJ
final case class FJ[H, Cond, T](h: H, cond: Cond, tail: T) extends FSHJ
