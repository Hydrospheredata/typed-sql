package typed.sql.internal

import shapeless._
import typed.sql.internal.FSHOps.FromInfer
import typed.sql._

//trait SelectInfer2[From, Q] {
//  type Out
//  def fields(shape: From): List[String]
//}
//
//trait LowPriotirySelectInfer2 {
//  type Aux[From, Q, Out0] = SelectInfer2[From, Q] { type Out = Out0 }
//
//
//}
//
//object SelectInfer2 extends LowPriotirySelectInfer2 {
//
//  implicit def forStar[S <: SHead](
//    implicit
//    fullValue:
//  ): Aux[S, All.type :: HNil, S] = {
//    new SelectInfer[S, R, All.type :: HNil] {
//      type Out = S
//      def fields(t: Table.Aux[S, R]): List[String] = t.columns
//    }
//  }
//
//}

trait SelectInfer2[A <: FSH, Q] {
  type Out
  def mkAst(shape: A): ast.Select[Out]
}

trait LowPrioSelectInfer2 {
  type Aux[A <: FSH, Q, Out0] = SelectInfer2[A, Q] { type Out = Out0 }

}

object SelectInfer2 extends LowPrioSelectInfer2 {

  implicit def forStar[A <: FSH, O](
    implicit
    allC: FSHOps.AllColumns[A],
    fromInf: FromInfer.Aux[A, All.type :: HNil, O]
  ): Aux[A, All.type :: HNil, O] = {
    new SelectInfer2[A, All.type :: HNil] {
      type Out = O
      def mkAst(shape: A): ast.Select[O] = {
        ast.Select(allC.columns, fromInf.mkAst(shape))
      }
    }
  }
}
