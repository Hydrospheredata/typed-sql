package typed.sql

import shapeless.{HList, LabelledGeneric}

sealed trait TR
final class TRepr[S, N, Rs <: HList, Ru <: HList] extends TR

//sealed trait TR2
//final class TRepr2[S, N, Rs <: HList, Ru <: HList] extends TR2


/**
  * FROM Table shape.
  * Describes plain tables|joins
  */
sealed trait FSH
final case class From[A <: TR](repr: A) extends FSH
sealed trait FSHJ[H <: TR, Cond <: JoinCond, T <: FSH] extends FSH {
  def repr: H
  def cond: Cond
  def tail: T
}

final case class IJ[H <: TR, Cond <: JoinCond, T <: FSH](repr: H, cond: Cond, tail: T) extends FSHJ[H, Cond, T]
final case class LJ[H <: TR, Cond <: JoinCond, T <: FSH](repr: H, cond: Cond, tail: T) extends FSHJ[H, Cond, T]
final case class RJ[H <: TR, Cond <: JoinCond, T <: FSH](repr: H, cond: Cond, tail: T) extends FSHJ[H, Cond, T]
final case class FJ[H <: TR, Cond <: JoinCond, T <: FSH](repr: H, cond: Cond, tail: T) extends FSHJ[H, Cond, T]



