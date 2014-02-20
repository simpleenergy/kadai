package kadai

import scalaz.Equal
import org.specs2.matcher.MustMatchers
import org.specs2.matcher.MatchResult

/**
 * adds mustEqual matcher that is defined in terms of the Equal typeclass
 */
trait EqualSyntax extends MustMatchers {
  implicit class EqualSyntax[A: Equal](a1: A) {
    def mustEqual(a2: A): MatchResult[Boolean] =
      Equal[A].equal(a1, a2) must beTrue
  }
}