package typed.sql

sealed trait JoinCond
object JoinCond {

  final case class Eq[K1, V, T1, K2, T2](
    c1: Column[K1, V, T1],
    c2: Column[K2, V, T2]
  ) extends JoinCond

  final case class And[A <: JoinCond, B <: JoinCond](a: A, b: B) extends JoinCond
  final case class Or[A <: JoinCond, B <: JoinCond](a: A, b: B) extends JoinCond
}
