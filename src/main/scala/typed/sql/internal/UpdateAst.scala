package typed.sql.internal

import shapeless._
import shapeless.ops.record.Selector
import typed.sql._

import scala.annotation.implicitNotFound

trait UpdateAst[A, In] {
  type Out
  def mkAst(in: In): List[ast.Set]
  def out(in: In): Out
}

object UpdateAst {
  
  @implicitNotFound("Couldn't prove that all fields used in `set` block are presented in table")
  type Aux[A, In, Out0] = UpdateAst[A, In] { type Out = Out0 }

  implicit def hnil[S]: Aux[S, HNil, HNil] =
    new UpdateAst[S, HNil] {
      type Out = HNil
      def mkAst(in: HNil): List[ast.Set] = List.empty
      def out(in: HNil): HNil = HNil
    }
  
  implicit def hCons[a, rs, Ru <: HList, K, v, S, T <: HList, OutX <: HList](
    implicit
    s2s: Symbol2Str[K],
    selector: Selector[Ru, K],
    next: UpdateAst.Aux[Table[a, rs, Ru], T, OutX],
  ): Aux[Table[a, rs, Ru], Assign[Column[K, v, Table[a, rs, Ru]], v] :: T, v :: OutX] =
    new UpdateAst[Table[a, rs, Ru], Assign[Column[K, v, Table[a, rs, Ru]], v] :: T] {
      type Out = v :: OutX
      
      def mkAst(in: Assign[Column[K, v, Table[a, rs, Ru]], v] :: T): List[ast.Set] = {
        val head = ast.Set(ast.Col(in.head.c.tableName, s2s.str))
        head :: next.mkAst(in.tail)
      }
      
      def out(in: Assign[Column[K, v, Table[a, rs, Ru]], v] :: T): v :: OutX =  in.head.v :: next.out(in.tail)
    }
}
