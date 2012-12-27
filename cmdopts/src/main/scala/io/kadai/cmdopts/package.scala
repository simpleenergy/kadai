package io.kadai

import collection.GenTraversableLike
import collection.generic.IsTraversableLike
import scalaz._
import Scalaz._

package object cmdopts {

  implicit class ValidationNELPimp[A](val v: ValidationNEL[String,A]) extends AnyVal {
      def &&[B]( other: ValidationNEL[String,B] ): ValidationNEL[String,(A,B)] = (v |@| other) tupled
  }
}
