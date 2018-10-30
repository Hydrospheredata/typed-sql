package typed.sql

sealed trait JoinType
object JoinType {

  sealed trait InnerJoin extends JoinType
  case object InnerJoin extends JoinType

  sealed trait LeftJoin extends JoinType
  case object LeftJoin extends JoinType

  sealed trait RightJoin extends JoinType
  case object RightJoin extends JoinType

  sealed trait FullJoin extends JoinType
  case object FullJoin extends JoinType

}
