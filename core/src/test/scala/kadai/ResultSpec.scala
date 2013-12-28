package kadai

import Invalid._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.scalacheck.{ Arbitrary, Gen, Prop }
import org.specs2.{ ScalaCheck, Specification }
import scalaz.{ Monoid, NonEmptyList, -\/ }
import scalaz.scalacheck.ScalazProperties._
import scalaz.syntax.std.boolean._

@RunWith(classOf[JUnitRunner])
class ResultSpec extends ScalaCheckSpec {
  import Result._
  import ArbitraryResult._

  def is = s2"""
  Result should
    invalid creatable from a String               $invalidFromString
    invalid creatable from a Throwable            $invalidFromThrowable
    invalid has a law abiding Monoid              ${checkAll(monoid.laws[Invalid])}
  """

  def invalidFromString =
    Prop.forAll { s: String =>
      (s.invalid must be equalTo Message(s)) and
        (s.invalidNel must be equalTo NonEmptyList(Message(s))) and
        (s.invalidResult must be equalTo -\/(Message(s)))
    }

  def invalidFromThrowable =
    Prop.forAll { s: Throwable =>
      (s.invalid must be equalTo Err(s)) and
        (s.invalidNel must be equalTo NonEmptyList(Err(s))) and
        (s.invalidResult must be equalTo -\/(Err(s)))
    }
}

object ArbitraryResult {
  import Arbitrary._
  import Result._
  private val rnd = new util.Random

  implicit def ArbitraryInvalid: Arbitrary[Invalid] =
    Arbitrary {
      rnd.nextBoolean ?
        arbitrary[Throwable].map { _.invalid } |
        arbitrary[String].map { _.invalid }
    }
}
