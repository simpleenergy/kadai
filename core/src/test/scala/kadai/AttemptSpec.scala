package kadai

import org.scalacheck.{ Arbitrary, Prop }
import scalaz.scalacheck.ScalazProperties._
import scalaz.std.anyVal._
import scalaz.syntax.id._

class AttemptSpec extends ScalaCheckSpec {
  import Result._
  import ArbitraryAttempt._

  def is = s2"""
  Attempt should
    be creatable from a String               $fromString
    be creatable from a Throwable            $fromThrowable
    be creatable from anything               $fromAny
    be safely creatable from exception       $safely
    be safely mappable                       $safelyMap
    be safely flatMappable                   $safelyFlatMap
    have a law abiding Monad                 ${checkAll(monad.laws[Attempt])}
  """

  def fromString =
    Prop.forAll { s: String =>
      Attempt.fail(s) must be equalTo Attempt(s.invalidResult)
    }

  def fromThrowable =
    Prop.forAll { t: Throwable =>
      Attempt.exception(t) must be equalTo Attempt(t.invalidResult)
    }

  def fromAny =
    Prop.forAll { a: Int =>
      Attempt.ok(a) must be equalTo Attempt(a.right)
    }

  def safely =
    checkSafe { identity }

  def safelyMap =
    checkSafe { _.map { _ + 11 } }

  def safelyFlatMap =
    checkSafe { _.flatMap { _ => Attempt.exception(new Error) } }

  private def checkSafe(f: Attempt[Int] => Attempt[Int]) =
    new RuntimeException |> { ex =>
      f { Attempt.safe { throw ex } } must be equalTo Attempt(ex.invalidResult)
    }
}

object ArbitraryAttempt {
  import Arbitrary._
  import Attempt._
  private val rnd = new util.Random

  implicit def arbitraryAttempt[A: Arbitrary]: Arbitrary[Attempt[A]] =
    Arbitrary {
      rnd.nextInt(10) match {
        case 0 => arbitrary[Throwable].map { exception }
        case 1 => arbitrary[String].map { fail }
        case i => arbitrary[A].map { ok }
      }
    }
}