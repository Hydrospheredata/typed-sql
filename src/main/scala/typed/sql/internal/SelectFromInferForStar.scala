package typed.sql.internal

import typed.sql.{Table, ast}

trait SelectFromInferForStar[A] {
  type Out
  def mkAst(a: A): ast.From
}

object SelectFromInferForStar {
  
  type Aux[A, Out0] = SelectFromInferForStar[A] { type Out = Out0 }
  
  implicit def forTable[A, rs, ru]: Aux[Table[A, rs, ru], A] = new SelectFromInferForStar[Table[A, rs, ru]] {
    override type Out = A
    override def mkAst(a: Table[A, rs, ru]): ast.From =  ast.From(a.name, List.empty)
  }
}
