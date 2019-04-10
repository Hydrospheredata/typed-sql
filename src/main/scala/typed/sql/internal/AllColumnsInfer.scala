package typed.sql.internal

import typed.sql.{From, Table, ast}

trait AllColumnsInfer[A] {
  def columns(a: A): List[ast.Col]
}

object AllColumnsInfer {
  
  private def create[A](f: A => List[ast.Col]): AllColumnsInfer[A] = new AllColumnsInfer[A] {
    override def columns(a: A): List[ast.Col] = f(a)
  }
  
  implicit def forTable[A, Rs, ru](implicit names: FieldNames[Rs]): AllColumnsInfer[Table[A, Rs, ru]] =
    create(table => names().map(n => ast.Col(table.name, n)))
  
  implicit def forFrom[A, Rs, ru](implicit names: FieldNames[Rs]): AllColumnsInfer[From[Table[A, Rs, ru]]] =
    create(from => names().map(n => ast.Col(from.table.name, n)))
  
}
