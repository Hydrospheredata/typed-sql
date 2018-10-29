package typed.sql.internal

trait JoinToTuple[A, B] {
  type Out
  def join(a: A, b: B): Out
}
