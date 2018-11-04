package typed.sql

import doobie.util.Read
import doobie.util.fragment.Fragment
import doobie.util.param.Param
import doobie.util.query.Query0
import doobie.util.update.Update0
import shapeless.HNil

object toDoobie {


  private def renderSel(s: ast.Select[_]): String = {

    def renderOrderBy(orderBy: ast.OrderBy): String = {
      val body = orderBy.values.map({case (col, ord) => {
        val o = ord match {
          case ast.DESC => "DESC"
          case ast.ASC => "ASC"
        }
        s"${col.table}.${col.name} $o"
      }}).mkString(",")
      s" ORDER BY $body"
    }

    def renderWCOnd(wc: ast.WhereCond): String = wc match {
      case ast.WhereEq(col) => s"${col.table}.${col.name} = ?"
      case ast.Less(col) => s"${col.table}.${col.name} < ?"
      case ast.LessOrEq(col) => s"${col.table}.${col.name} <= ?"
      case ast.Gt(col) => s"${col.table}.${col.name} > ?"
      case ast.GtOrEq(col) => s"${col.table}.${col.name} >= ?"
      case ast.Like(col) => s"${col.table}.${col.name} like ?"
      case ast.And(c1, c2) => renderWCOnd(c1) + " AND " + renderWCOnd(c2)
      case ast.Or(c1, c2) => renderWCOnd(c1) + " OR " + renderWCOnd(c2)
    }
    def joinCondRender(jc: ast.JoinCond): String = jc match {
      case ast.JoinCondEq(col1, col2) => s"${col1.table}.${col1.name} = ${col2.table}.${col2.name}"
      case ast.JoinCondAnd(c1, c2) => joinCondRender(c1) + " AND " + joinCondRender(c2)
      case ast.JoinCondOr(c1, c2) => joinCondRender(c1) + " OR " + joinCondRender(c2)
    }

    def joinRender(j: ast.Join): String = j match {
      case ast.InnerJoin(t, c) => s"INNER JOIN $t ON " + joinCondRender(c)
      case ast.LeftJoin(t, c) => s"LEFT JOIN $t ON " + joinCondRender(c)
      case ast.RightJoin(t, c) => s"RIGHT JOIN $t ON " + joinCondRender(c)
      case ast.FullJoin(t, c) =>  s"FULL JOIN $t ON " + joinCondRender(c)
    }

    val cols = s.cols.map(c => s"${c.table}.${c.name}").mkString(", ")
    val joins = s.from.joins.map(joinRender).mkString(" ")
    val from = s.from.table
    val whereR = s.where.map(c => " WHERE " + renderWCOnd(c)).getOrElse("")
    val order = s.orderBy.map(o => renderOrderBy(o)).getOrElse("")

    //TODO: move to params!
    val limitOpt = s.limit.map(v => s" LIMIT ?").getOrElse("")
    val offsetOpt = s.offset.map(v => s" OFFSET ?").getOrElse("")
    s"SELECT $cols FROM $from $joins" + whereR + order + limitOpt + offsetOpt
  }

  private def renderDel(d: ast.Delete): String = {
    def renderWCOnd(wc: ast.WhereCond): String = wc match {
      case ast.WhereEq(col) => s"${col.name} = ?"
      case ast.Less(col) => s"${col.name} < ?"
      case ast.LessOrEq(col) => s"${col.name} <= ?"
      case ast.Gt(col) => s"${col.name} > ?"
      case ast.GtOrEq(col) => s"${col.name} >= ?"
      case ast.Like(col) => s"${col.name} like ?"
      case ast.And(c1, c2) => renderWCOnd(c1) + " AND " + renderWCOnd(c2)
      case ast.Or(c1, c2) => renderWCOnd(c1) + " OR " + renderWCOnd(c2)
    }

    val whereR = d.where.map(c => " WHERE " + renderWCOnd(c)).getOrElse("")
    s"DELETE FROM ${d.table}" + whereR
  }

  private def renderUpd(upd: ast.Update): String = {
    def renderWCOnd(wc: ast.WhereCond): String = wc match {
      case ast.WhereEq(col) => s"${col.name} = ?"
      case ast.Less(col) => s"${col.name} < ?"
      case ast.LessOrEq(col) => s"${col.name} <= ?"
      case ast.Gt(col) => s"${col.name} > ?"
      case ast.GtOrEq(col) => s"${col.name} >= ?"
      case ast.Like(col) => s"${col.name} like ?"
      case ast.And(c1, c2) => renderWCOnd(c1) + " AND " + renderWCOnd(c2)
      case ast.Or(c1, c2) => renderWCOnd(c1) + " OR " + renderWCOnd(c2)
    }

    val whereR = upd.where.map(c => " WHERE " + renderWCOnd(c)).getOrElse("")
    val sets = upd.sets.map(s => s"${s.col.name} = ?").mkString(", ")
    s"UPDATE ${upd.table} SET $sets" + whereR
  }

  private def renderInsInto(ins: ast.InsertInto): String = {
    val columns = ins.columns.map(c => c.name).mkString("(", ",", ")")
    val values = ins.columns.map(_ => "?").mkString("(", ",", ")")
    s"INSERT INTO ${ins.table} $columns VALUES $values"
  }

  implicit class WrapSelection[S <: FSH, Out, In](sel: Selection[S, Out, In]) {

    def toFragment(implicit param: Param[In]): Fragment = {
      val sql = renderSel(sel.astData)
      Fragment[In](sql, sel.in, None)(param.write)
    }

    def toQuery(implicit param: Param[In], read: Read[Out]): Query0[Out] = toFragment.query[Out](read)
  }

  implicit class WrapDeletion[S <: FSH, In](del: Deletion[S, In]) {

    def toFragment(implicit param: Param[In]): Fragment = {
      val sql = renderDel(del.astData)
      Fragment[In](sql, del.in, None)(param.write)
    }

    def toUpdate(implicit param: Param[In]): Update0 = toFragment.update
  }

  implicit class WrapUpdation[S <: FSH, In](upd: Updation[S, In]) {

    def toFragment(implicit param: Param[In]): Fragment = {
      val sql = renderUpd(upd.astData)
      Fragment[In](sql, upd.in, None)(param.write)
    }

    def toUpdate(implicit param: Param[In]): Update0 = toFragment.update
  }

  implicit class WrapIns[In](ins: Insert[In]) {

    def toFragment(implicit param: Param[In]): Fragment = {
      val sql = renderInsInto(ins.astData)
      Fragment[In](sql, ins.in, None)(param.write)
    }

    def toUpdate(implicit param: Param[In]): Update0 = toFragment.update
  }
}
