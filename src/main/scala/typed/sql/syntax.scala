package typed.sql

import shapeless.LabelledGeneric.Aux
import shapeless.{HList, HNil, LabelledGeneric, ProductArgs, Witness}
import typed.sql.Selection.HasWhere
import typed.sql.internal.{SelectInfer, WhereInfer}


object syntax extends ColumnSyntax {

  object select extends ProductArgs {
    def applyProduct[A](query: A): SelectionPrefix[A] = SelectionPrefix(query)
  }

  val `*` = All

  implicit class WhereSyntax[S, R, Repr1 <: HList, In1 <: HList](
    selection: Selection.Aux[S, R, Repr1, In1, Selection.WithoutWhere.type]
  ) {

    def where[C <: WhereClause, Out <: HList](clause: C)(
      implicit whereInfer: WhereInfer.Aux[Repr1, C, Out]
    ): Selection[S, R] = {
      new Selection[S, R] {

        type Repr = Repr1

        type In = Out

        type WhereFlag = Selection.WhereDefined.type

        val labelledGeneric: LabelledGeneric.Aux[S, Repr] = selection.labelledGeneric

        val tableName = selection.tableName
        val fields = selection.fields

        val sql: String = {
          val fs = fields.map(f => s"$tableName.$f").mkString(", ")
          val rendered = whereInfer.expr.render(tableName)
          s"SELECT $fs FROM $tableName WHERE $rendered"
        }

        val in: In = whereInfer.in(clause)
      }
    }
  }
}
