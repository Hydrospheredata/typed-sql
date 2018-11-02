package typed.sql

trait Updation[In] {
  def astData: ast.Update
  def in: In
}
