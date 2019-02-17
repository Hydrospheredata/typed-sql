package typed.sql.internal

import shapeless._
import shapeless.ops.hlist.Tupler
//import typed.sql.internal.FSHOps.{FromInfer, FromInferForStarSelect, LowLevelSelectInfer}
import typed.sql._

import scala.annotation.implicitNotFound

@implicitNotFound("Couldn't infer selection:\n  Given: ${A},\n  Query: ${Q}.")
trait SelectInfer[A, Q] {
  type Out
  def mkAst(shape: A): ast.Select[Out]
}

trait LowPrioSelectInfer {
  @implicitNotFound("Couldn't infer selection\n  Given: ${A},\n  Query: ${Q}.")
  type Aux[A, Q, Out0] = SelectInfer[A, Q] { type Out = Out0 }
//
//  implicit def columns[A <: FSH, H <: HList, O <: HList](
//    implicit
//    llsi: LowLevelSelectInfer.Aux[A, H, O],
//    fInf: FromInfer[A]
//  ): Aux[A, H, O] = {
//    new SelectInfer[A, H] {
//      type Out = O
//      override def mkAst(shape: A): ast.Select[O] = {
//        ast.Select(llsi.cols, fInf.mkAst(shape), None, None, None, None)
//      }
//    }
//  }
}
//
object SelectInfer extends LowPrioSelectInfer {
//
//  implicit def forStar[A <: FSH, O](
//    implicit
//    allC: FSHOps.AllColumns[A],
//    fromInf: FromInferForStarSelect.Aux[A, O]
//  ): Aux[A, All.type :: HNil, O] = {
//    new SelectInfer[A, All.type :: HNil] {
//      type Out = O
//      def mkAst(shape: A): ast.Select[O] = {
//        ast.Select(allC.columns, fromInf.mkAst(shape), None, None, None, None)
//      }
//    }
//  }
}
