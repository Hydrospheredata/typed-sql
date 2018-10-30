package typed.sql

object ast {

  case class Col(table: String, name: String)

  sealed trait JoinCond
  final case class JoinCondEq(col1: Col, col2: Col) extends JoinCond
  final case class JoinCondAnd(c1: JoinCond, c2: JoinCond) extends JoinCond
  final case class JoinCondOr(c1: JoinCond, c2: JoinCond) extends JoinCond


  sealed trait Join {
    def table: String
    def cond: JoinCond
  }
  final case class InnerJoin(table: String, cond: JoinCond) extends Join
  final case class LeftJoin(table: String, cond: JoinCond) extends Join
  final case class RightJoin(table: String, cond: JoinCond) extends Join
  final case class FullJoin(table: String, cond: JoinCond) extends Join

  case class From(table: String, joins: List[Join])
  case class Select[Out](cols: List[Col], from: From)
}
