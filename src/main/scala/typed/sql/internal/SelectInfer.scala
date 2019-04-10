package typed.sql.internal

import shapeless._
import typed.sql._

import scala.annotation.implicitNotFound

trait SelectInfer[A, Q] {
  type Out
  def mkAst(shape: A, fields: Q): ast.Select[Out]
}


trait LowPrioSelectInfer {
  
  @implicitNotFound("Couldn't infer selection")
  type Aux[A, Q, Out0] = SelectInfer[A, Q] { type Out = Out0 }
  
  implicit def columns[A, H, O](
    implicit
    fieldsInf: SelectFieldsInfer.Aux[A, H, O],
    fromInf: SelectSimpleFromInfer[A]
  ): Aux[A, H, O] = {
    new SelectInfer[A, H] {
      type Out = O
      def mkAst(shape: A, fields: H): ast.Select[O] = {
        ast.Select(fieldsInf.mkAst(fields), fromInf.mkAst(shape), None, None, None, None)
      }
    }
  }
}

object SelectInfer extends LowPrioSelectInfer {

  implicit def forStar[A, O](
    implicit
    columnsInf: AllColumnsInfer[A],
    fromInf: SelectFromInferForStar.Aux[A, O]
  ): Aux[A, All.type :: HNil, O] = {
    new SelectInfer[A, All.type :: HNil] {
      type Out = O
      def mkAst(shape: A, fields: All.type :: HNil): ast.Select[O] = {
        ast.Select(columnsInf.columns(shape), fromInf.mkAst(shape), None, None, None, None)
      }
    }
  }
}
