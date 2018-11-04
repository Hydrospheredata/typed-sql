package typed.sql

object ast {

  case class Col(table: String, name: String)

  sealed trait JoinCond extends Product with Serializable
  final case class JoinCondEq(col1: Col, col2: Col) extends JoinCond
  final case class JoinCondAnd(c1: JoinCond, c2: JoinCond) extends JoinCond
  final case class JoinCondOr(c1: JoinCond, c2: JoinCond) extends JoinCond


  sealed trait Join extends Product with Serializable {
    def table: String
    def cond: JoinCond
  }
  final case class InnerJoin(table: String, cond: JoinCond) extends Join
  final case class LeftJoin(table: String, cond: JoinCond) extends Join
  final case class RightJoin(table: String, cond: JoinCond) extends Join
  final case class FullJoin(table: String, cond: JoinCond) extends Join

  sealed trait WhereCond extends Product with Serializable
  final case class WhereEq(col: Col) extends WhereCond
  final case class Less(col: Col) extends WhereCond
  final case class LessOrEq(col: Col) extends WhereCond
  final case class Gt(col: Col) extends WhereCond
  final case class GtOrEq(col: Col) extends WhereCond
  final case class Like(col: Col) extends WhereCond
  final case class And(c1: WhereCond, c2: WhereCond) extends WhereCond
  final case class Or(c1: WhereCond, c2: WhereCond) extends WhereCond

  sealed trait SortOrder
  case object ASC extends SortOrder
  case object DESC extends SortOrder
  case class OrderBy(values: List[(Col, SortOrder)])

  case class From(table: String, joins: List[Join])

  case class Select[Out](
    cols: List[Col],
    from: From,
    where: Option[WhereCond],
    orderBy: Option[OrderBy],
    limit: Option[Int],
    offset: Option[Int]
  )

  case class Delete(table: String, where: Option[WhereCond])

  case class Set(col: Col)
  case class Update(table: String, sets: List[Set], where: Option[WhereCond])

  case class InsertInto(table: String, columns: List[Col])
}
