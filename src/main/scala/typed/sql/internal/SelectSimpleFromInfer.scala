package typed.sql.internal

import typed.sql.{From, Table, ast}

trait SelectSimpleFromInfer[A] {
  def mkAst(a: A): ast.From
}

object SelectSimpleFromInfer {
  
  def create[A](f: A => ast.From): SelectSimpleFromInfer[A] =
    new SelectSimpleFromInfer[A] {
      override def mkAst(a: A): ast.From = f(a)
    }
  
  implicit def forTable[A, rs, ru]: SelectSimpleFromInfer[Table[A, rs, ru]] =
    SelectSimpleFromInfer.create(table => ast.From(table.name, List.empty))
  
  implicit def forFrom[A, rs, ru]: SelectSimpleFromInfer[From[Table[A, rs, ru]]] =
    SelectSimpleFromInfer.create(from => ast.From(from.table.name, List.empty))
}

