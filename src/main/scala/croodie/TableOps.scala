package croodie

import doobie._
import doobie.syntax._
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.update.Update0
import shapeless.labelled.FieldType
import shapeless.ops.hlist.{Diff, Mapper, Remove, RemoveAll, Selector, ToTraversable}
import shapeless.ops.record.{Fields, Keys}
import shapeless.{DepFn1, DepFn2, Generic, HList, HNil, LabelledGeneric, Poly1, RecordArgs, Witness}
import shapeless._

//
//trait QuerySelect[From, El] {
//  type Out
//}
//
//trait LowPrio {
//
//  type Aux[From, El, Out0] = QuerySelect[From, El] { type Out = Out0 }
//
//  implicit def tuple[A, H <: HList, From](
//    implicit
//    gen: Generic.Aux[A, H],
//    sel: QuerySelect[From, H]
//  ): QuerySelect.Aux[From, A, H] = {
//
//    new QuerySelect[From, A] {
//      type Out = H
//    }
//  }
//}
//
//object QuerySelect extends LowPrio {
//
//  implicit def hNil[From]: QuerySelect.Aux[From, HNil, HNil] = {
//    new QuerySelect[From, HNil] {
//      type Out = HNil
//    }
//  }
//
//  implicit def hlist[H, T <: HList, From <: HList, Z <: HList, X](
//    implicit
//    hSel: Selector[From, H],
//    tSel: QuerySelect.Aux[From, T, Z]
//  ): QuerySelect.Aux[From, H :: T,  hSel.Out :: Z] = {
//    new QuerySelect[From, H :: T] {
//      type Out = hSel.Out :: Z
//    }
//  }
//
//}
//
//sealed trait Clause[K]
//case class Eq[K, T](k: K, v: T) extends Clause[K]
//
//trait WhereSelect[From, El] {
//  type Out
//}
//
//trait LowPrioW {
//
//  type Aux[From, El, Out0] = WhereSelect[From, El] { type Out = Out0 }
//
//  implicit def tuple[A, H, From](
//    implicit
//    gen: Generic.Aux[A, H],
//    sel: WhereSelect[From, H]
//  ): WhereSelect.Aux[From, A, H] = {
//
//    new WhereSelect[From, A] {
//      type Out = H
//    }
//  }
//}
//
//object WhereSelect extends LowPrioW {
//
//  implicit def hNil[From]: WhereSelect.Aux[From, HNil, HNil] = {
//    new WhereSelect[From, HNil] {
//      type Out = HNil
//    }
//  }
//
//  implicit def hlist[H, T <: HList, From <: HList, Z <: HList, X](
//    implicit
//    ev: H <:< Clause[X],
//    hSel: Selector[From, X],
//    tSel: WhereSelect.Aux[From, T, Z]
//  ): WhereSelect.Aux[From, H:: T, hSel.Out :: Z] = {
//    new WhereSelect[From, H :: T] {
//      type Out = hSel.Out :: Z
//    }
//  }
//
//}
//
//class Select[Query <: HList, All <: HList] {
//
//  def where[R](r: R)(implicit whereSelect: WhereSelect[All, R]): WhereSelect[All, R] = whereSelect
//
//  def mkFragment(implicit names: FieldNames[All]): Fragment = {
//    val str = "SELECT " + names().mkString(", ") + " FROM table"
//    Fragment[HNil](str, HNil, None)(Param.ParamHNil.write)
//  }
//
//}
//
//object toName extends Poly1 {
//  implicit def keyToName[A] = at[Symbol with A](_.name)
//}
//
//trait TableData[Fields <: HList] {
//
//}
//
//
//class Table[A, All <: HList] {
//  def select[R, Z <: HList](r: R)(implicit querySelect: QuerySelect.Aux[All, R, Z]): Select[Z, All] = new Select[Z, All]
//}
//
//object Table {
//
//  class TableBuilder[A, Fields <: HList]() {
//
//    def idColumn(c: Witness)(implicit sel: Selector[Fields, c.T]): Table[A, Fields] = {
//      new Table[A, Fields]
//    }
//  }
//
//
//  object of {
//    def apply[A] = new BuilderUnapplied[A]
//
//    class BuilderUnapplied[A] {
//      def apply[H <: HList, F <: HList](
//        implicit labelledGeneric: LabelledGeneric.Aux[A, H],
//        fields: Fields.Aux[H, F]
//      ): TableBuilder[A, F] = {
//        new TableBuilder[A, F]()
//      }
//    }
//  }
//
//}
