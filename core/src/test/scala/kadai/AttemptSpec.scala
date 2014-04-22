package kadai

import org.scalacheck.{ Arbitrary, Prop }
import scalaz.Equal
import scalaz.scalacheck.ScalazArbitrary._
import scalaz.scalacheck.ScalazProperties._
import scalaz.std.anyVal._
import scalaz.std.option._
import scalaz.syntax.either._
import scalaz.syntax.ToIdOps
import scalaz.syntax.std.option._

class AttemptSpec extends ScalaCheckSpec with EqualSyntax with ToIdOps {
  import Result._
  import ArbitraryAttempt._
  import ArbitraryResult.ArbitraryInvalid

  def is = s2"""
  Attempt should
    be creatable from a String               $fromString
    be creatable from a Throwable            $fromThrowable
    be creatable from anything               $fromAny
    be safely creatable from exception       $safely
    be safely mappable                       $safelyMap
    be safely flatMappable                   $safelyFlatMap
    run lifted Result functions              $liftResultFn
    convert to option                        $toOption
    convert to disjunction                   $toOr
    have a law abiding Monad                 ${checkAll(monad.laws[Attempt])}
    have a law abiding Equal                 ${checkAll(equal.laws[Attempt[Int]])}
    have a law abiding Monoid                ${checkAll(monoid.laws[Attempt[Int]])}
  """

  def fromString =
    Prop.forAll { s: String =>
      Attempt.fail(s) === Attempt(s.invalidResult)
    }

  def fromThrowable =
    Prop.forAll { t: Throwable =>
      Attempt.exception(t) === Attempt(t.invalidResult)
    }

  def fromAny =
    Prop.forAll { a: Int =>
      Attempt.ok(a) === Attempt(a.right)
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

  def liftResultFn =
    Prop.forAll { (fa: Attempt[Int], f: Result[Int] => Result[Int]) =>
      Attempt(f(fa.run)) mustEqual fa.lift(f)
    }

  def toOr =
    Prop.forAll { i: Int =>
      (Attempt.ok(i).toOr mustEqual i.right) and (Attempt.ToOr(Attempt.ok(i)) mustEqual i.right)
    }

  def toOption =
    Prop.forAll { i: Int =>
      (Attempt.ok(i).toOption mustEqual i.some) and (Attempt.ToOption(Attempt.ok(i)) mustEqual i.some)
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
