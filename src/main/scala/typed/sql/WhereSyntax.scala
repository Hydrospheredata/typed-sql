package typed.sql

import shapeless.{::, HList, HNil}
import shapeless.ops.adjoin.Adjoin
import typed.sql.internal.WhereAst

import scala.annotation.implicitNotFound

trait UseWhere[A, C] {
  type Out
  def apply(a: A, c: C): Out
}

object UseWhere {
  @implicitNotFound("Can't use where here. Ensure that condition is correct and was used only once")
  type Aux[A, C, O] = UseWhere[A, C] { type Out = O }
  
  def apply[A, C, O](implicit instance: UseWhere.Aux[A, C, O]): UseWhere.Aux[A, C, O] = instance
}

trait DefaultUseWhereInstances$ {

  implicit def forUpdate[T, C, In0 <: HList, In1 <: HList, O <: HList](implicit
    inf: WhereAst.Aux[T, C, In1],
    adjoin: Adjoin.Aux[In0 :: In1 :: HNil, O]
  ): UseWhere.Aux[Update[T, In0, WhereFlag.NotUsed], C, Update[T, O, WhereFlag.Used]] =
    new UseWhere[Update[T, In0, WhereFlag.NotUsed], C] {
      type Out = Update[T, O, WhereFlag.Used]
      def apply(a: Update[T, In0, WhereFlag.NotUsed], c: C): Update[T, O, WhereFlag.Used] = {

        new Update[T, O, WhereFlag.Used] {
          override def astData: ast.Update = a.astData.copy(where = Some(inf.mkAst(c)))
          override def params: O = adjoin(a.params :: inf.params(c) :: HNil)
        }
      }
  
    }
  
  implicit def forDelete[T, C, In](implicit
    inf: WhereAst.Aux[T, C, In],
  ): UseWhere.Aux[Delete[T, HNil, WhereFlag.NotUsed], C, Delete[T, In, WhereFlag.Used]] = {

    new UseWhere[Delete[T, HNil, WhereFlag.NotUsed], C] {
      type Out = Delete[T, In, WhereFlag.Used]
      def apply(a: Delete[T, HNil, WhereFlag.NotUsed], c: C): Delete[T, In, WhereFlag.Used] = {
        new Delete[T, In, WhereFlag.Used] {
          def astData: ast.Delete = a.astData.copy(where = Some(inf.mkAst(c)))
          def params: In = inf.params(c)
        }
      }
    }
  }
  
  implicit def forSelect[S, C, In0, ]
  
  def where[C <: WhereCond, In0 <: HList](c: C)(implicit inf: WhereAst.Aux[S, C, In0]): Select[S, O, In0] =
    new Select[S, O, In0] {
      type WhereFlag = Select.WhereDefined.type
      def astData: ast.Select[O] = selection.astData.copy(where = Some(inf.mkAst(c)))
      def in: In0 = inf.params(c)
    }
}


object DefaultUseWhereInstances$ extends DefaultUseWhereInstances$

trait WhereSyntax {
  implicit final def toWhereWord[A](a: A): WhereWord[A] = new WhereWord[A](a)
  implicit final def toBom[A](a: A): Bombom[A] = new Bombom[A](a)
}

final class WhereWord[A](val a: A) extends AnyVal {
  def where[C, Out](c: C)(implicit useWhere: UseWhere.Aux[A, C, Out]): Out = useWhere(a, c)
}

final class Bombom[A](val a: A) extends AnyVal {
  def bombom[X](x: X): A = a
}


