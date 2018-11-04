package typed.sql

import cats.Reducible
import doobie.enum.Nullability.NoNulls
import doobie.util.{Put, Write}
import doobie.util.fragment.Fragment
import doobie.util.param.Param
import shapeless._

trait MkWrite[A] {
  def apply(a: A): Write[A]
}

object MkWrite {

  implicit def fromParam[A](implicit p: Param[A]): MkWrite[A] =
    new MkWrite[A] {
      def apply(a: A): Write[A] = p.write
    }

  implicit def list[A](implicit P: Put[A]): MkWrite[List[A]] = {
    new MkWrite[List[A]] {
      override def apply(a: List[A]): Write[List[A]] = {
        new Write[List[A]](
          a.map(_ => P -> NoNulls),
          a => a,
          (ps, n, a) => {
            a.indices.foreach(i => {
              P.unsafeSetNonNullable(ps, n + i, a(i))
            })
          },
          (rs, n, a) => {
            a.indices.foreach(i => {
              P.unsafeUpdateNonNullable(rs, n + i, a(i))
            })
          }
        )
      }
    }
  }

  implicit def hnil: MkWrite[HNil] = new MkWrite[HNil] {
    override def apply(a: HNil): Write[HNil] = Write.emptyProduct
  }

  implicit def hcons[H, T <: HList](
    implicit
    H: Lazy[MkWrite[H]],
    T: Lazy[MkWrite[T]]
  ): MkWrite[H :: T] = {
    new MkWrite[H :: T] {
      override def apply(a: H :: T): Write[H :: T] = {
        val w1 = H.value(a.head)
        val w2 = T.value(a.tail)

        new Write(
          w1.puts ++ w2.puts,
          { case h :: t => w1.toList(h) ++ w2.toList(t) },
          { case (ps, n, h :: t) => w1.unsafeSet(ps, n, h); w2.unsafeSet(ps, n + w1.length, t) },
          { case (rs, n, h :: t) => w1.unsafeUpdate(rs, n, h); w2.unsafeUpdate(rs, n + w1.length, t) }
        )
      }
    }
  }
}


