package typed.sql

trait Updation[S <: FSH, In] {
  def astData: ast.Update
  def in: In
}
