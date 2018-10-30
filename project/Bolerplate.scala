import sbt._

/**
  * Copied, with some modifications, from https://github.com/milessabin/shapeless/blob/master/project/Boilerplate.scala
  *
  * Generate a range of boilerplate classes, those offering alternatives with 0-22 params
  * and would be tedious to craft by hand
  */
object Boilerplate {

  import scala.StringContext._

  implicit class BlockHelper(val sc: StringContext) extends AnyVal {
    def block(args: Any*): String = {
      val interpolated = sc.standardInterpolator(treatEscapes, args)
      val rawLines = interpolated split '\n'
      val trimmedLines = rawLines map {
        _ dropWhile (_.isWhitespace)
      }
      trimmedLines mkString "\n"
    }
  }


  val templates: Seq[Template] = List(GenTupleAppend)

  object GenTupleAppend extends Template {
    val filename = "TupleAppendInstances.scala"

    override def range: Range.Inclusive = 1 to 21
    override def content(tv: TemplateVals): String = {
      import tv._
      block"""
          |package typed.sql.internal
          |
          |trait TupleAppendInstances extends LowPrioTupleAppend {
          |
          -  implicit def tApp${arity}[${`A..N`}, ${`N+1`}]: Aux[${`(A..N)`}, ${`N+1`}, ${`(A..N+1)`}] = null
          |}
      """
    }
  }

  /** Returns a seq of the generated files.  As a side-effect, it actually generates them... */
  def gen(dir: File) = for (t <- templates) yield {
    val tgtFile = dir / t.packageName / t.filename
    IO.write(tgtFile, t.body)
    tgtFile
  }

  trait Template { self =>

    def packageName: String = "typed/sql/internal"

    def createVals(arity: Int): TemplateVals = new TemplateVals(arity)

    def filename: String
    def content(tv: TemplateVals): String
    def range = 1 to 22
    def body: String = {
      val rawContents = range map { n => content(createVals(n)) split '\n' filterNot (_.isEmpty) }
      val preBody = rawContents.head takeWhile (_ startsWith "|") map (_.tail)
      val instances = rawContents flatMap {_ filter (_ startsWith "-") map (_.tail) }
      val postBody = rawContents.head dropWhile (_ startsWith "|") dropWhile (_ startsWith "-") map (_.tail)
      (preBody ++ instances ++ postBody) mkString "\n"
    }
  }

  class TemplateVals(val arity: Int) {
    val synTypes     = (0 until arity) map (n => (n+'A').toChar)
    val synVals      = (0 until arity) map (n => (n+'a').toChar)
    val synTypedVals = (synVals zip synTypes) map { case (v,t) => v + ":" + t}

    val `A..N`       = synTypes.mkString(", ")
    val `N+1`        = (arity + 'A').toChar
    val `A..N,Res`   = (synTypes :+ "Res") mkString ", "
    val `a..n`       = synVals.mkString(", ")
    val `A::N`       = (synTypes :+ "HNil") mkString "::"
    val `a::n`       = (synVals :+ "HNil") mkString "::"
    val `_.._`       = Seq.fill(arity)("_").mkString(", ")
    val `(A..N)`     = if (arity == 1) "Tuple1[A]" else synTypes.mkString("(", ", ", ")")
    val `(A..N+1)`     = if (arity == 1) "Tuple1[A]" else (synTypes :+ `N+1`).mkString("(", ", ", ")")
    val `(_.._)`     = if (arity == 1) "Tuple1[_]" else Seq.fill(arity)("_").mkString("(", ", ", ")")
    val `(a..n)`     = if (arity == 1) "Tuple1(a)" else synVals.mkString("(", ", ", ")")
    val `(a._1::a._n)`  = ((1 to arity).map(i => s"a._$i") :+ "HNil").mkString("::")
    val `a:A..n:N`   = synTypedVals mkString ", "
  }
}