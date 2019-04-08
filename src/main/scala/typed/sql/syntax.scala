package typed.sql

import shapeless._
import shapeless.ops.adjoin.Adjoin
import typed.sql.internal.{OrderByInfer, WhereAst}
import typed.sql.prefixes._

object syntax
  extends ColumnSyntax
  with DeleteSyntax
  with UpdateSyntax
  with InsertIntoSyntax
  with SelectSyntax
  with WhereSyntax
  with DefaultUseWhereInstances {


//  implicit class WhereSelectSyntax[S <: FSH, O](selection: Select[S, O, HNil]) {
//
//    def where[C <: WhereCond, In0 <: HList](c: C)(implicit inf: WhereAst.Aux[S, C, In0]): Select[S, O, In0] =
//      new Select[S, O, In0] {
//        type WhereFlag = Select.WhereDefined.type
//        def astData: ast.Select[O] = selection.astData.copy(where = Some(inf.mkAst(c)))
//        def in: In0 = inf.params(c)
//      }
//  }
//
//  implicit class OrderBySelectSyntax[S <: FSH, O, In](sel: Select[S, O, In]) {
//
//    object orderBy extends ProductArgs {
//      def applyProduct[C <: HList](c: C)(implicit inf: OrderByInfer[S, C]): Select[S, O, In] = {
//        new Select[S, O, In] {
//          type WhereFlag = sel.WhereFlag
//          val astData: ast.Select[O] = {
//            val ord = ast.OrderBy(inf.columns)
//            sel.astData.copy(orderBy = Some(ord))
//          }
//          val in: In = sel.in
//        }
//      }
//    }
//  }
//
//  implicit class LimitOffsetSyntax[S <: FSH, O, In](sel: Select[S, O, In]) {
//
//    def limit[J <: HList](i: Int)(implicit adjoin: Adjoin.Aux[In :: Int :: HNil, J]): Select[S, O, J] =
//        new Select[S, O, J] {
//          type WhereFlag = sel.WhereFlag
//          val astData: ast.Select[O] = {
//            sel.astData.copy(limit = Some(i))
//          }
//          val in: J = adjoin(sel.in :: i :: HNil)
//        }
//
//    def offset[J <: HList](i: Int)(implicit adjoin: Adjoin.Aux[In :: Int :: HNil, J]): Select[S, O, J] =
//      new Select[S, O, J] {
//        type WhereFlag = sel.WhereFlag
//        val astData: ast.Select[O] = {
//          sel.astData.copy(offset = Some(i))
//        }
//        val in: J = adjoin(sel.in :: i :: HNil)
//      }
//  }

//  implicit class JoinSyntax[A <: FSH](shape: A) {
//
//    def innerJoin[S2, Rs2 <: HList, ru <: HList](t: Table[S2, Rs2, ru]): IJPrefix[A, S2, Rs2, ru] =
//      new IJPrefix(shape, From(t.repr))
//
//    def leftJoin[S2, Rs2 <: HList, ru <: HList](t: Table[S2, Rs2, ru]): LJPrefix[A, S2, Rs2, ru] =
//      new LJPrefix(shape, From(t.repr))
//
//    def rightJoin[S2, Rs2 <: HList, ru <: HList](t: Table[S2, Rs2, ru]): RJPrefix[A, S2, Rs2, ru] =
//      new RJPrefix(shape, From(t.repr))
//
//    def fullJoin[S2, Rs2 <: HList, ru <: HList](t: Table[S2, Rs2, ru]): FJPrefix[A, S2, Rs2, ru] =
//      new FJPrefix(shape, From(t.repr))
//  }

}
