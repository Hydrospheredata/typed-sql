package typed.sql

case class Insert[In](astData: ast.InsertInto, in: In)

