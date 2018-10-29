package typed.sql

import shapeless._
import typed.sql.internal.{SelectInfer2}

case class SelectionPrefix[A](query: A) {

//  def from[S, Repr1 <: HList, O](t: Table[S])(
//    implicit
//    selInfer: SelectInfer.Aux[S, t.Repr, A, O]
//  ): Selection[S, O] { type Repr = Repr1; type In = HNil; type WhereFlag = Selection.WithoutWhere.type } = {
//    new Selection[S, O] {
//      type Repr = t.Repr
//
//      type In = HNil
//
//      type WhereFlag = Selection.WithoutWhere.type
//
//      val labelledGeneric: LabelledGeneric.Aux[S, Repr] = t.labelledGeneric
//
//      val tableName = t.name
//      val fields = selInfer.fields(t)
//
//      val sql: String = {
//        val fs = fields.map(f => s"$tableName.$f").mkString(", ")
//        s"SELECT $fs FROM $tableName"
//      }
//
//      val in: In = HNil
//    }
//  }
//

}

