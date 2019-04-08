package typed.sql.internal

import typed.sql.{From, Table, ast}

trait SelectFromInfer[A] {
  def mkAst(a: A): ast.From
}

object SelectFromInfer {
  
  def create[A](f: A => ast.From): SelectFromInfer[A] =
    new SelectFromInfer[A] {
      override def mkAst(a: A): ast.From = f(a)
    }
  
  implicit def forTable[A, rs, ru]: SelectFromInfer[Table[A, rs, ru]] =
    SelectFromInfer.create(table => ast.From(table.name, List.empty))
  
  implicit def forFrom[A, rs, ru]: SelectFromInfer[From[Table[A, rs, ru]]] =
    SelectFromInfer.create(from => ast.From(from.table.name, List.empty))
}

trait DefaultSelectFromInferInstances {
  

}
