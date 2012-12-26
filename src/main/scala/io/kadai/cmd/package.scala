package io.kadai

import collection.GenTraversableLike
import collection.generic.IsTraversableLike

package object cmd {

  implicit class TraversableOptionalSyntax[Repr <: GenTraversableLike[_, Repr]](val r: Repr) extends AnyVal {
    //type A = ???
    def notEmpty: Option[Repr] =
      if (r.isEmpty) None else Some(r)

    def tailOption: Option[Repr] =
      notEmpty flatMap { r => new TraversableOptionalSyntax(r.tail).notEmpty }

    // experiment, needs to be: (A, Repr) currently: (Any, Repr)
    def headTailOption: Option[(Any, Repr)] =
       notEmpty map { r => r.head -> r.tail }

    private def map[A](so: => A): Option[A] =
      if (r.isEmpty) None else Some(so)

    private def flatMap[A](so: => Option[A]): Option[A] =
      if (r.isEmpty) None else so
  }

  // somehow we need to attempt to capture the IsTraversableLike's head type
  //implicit def ToTailOption[A, R<: GenTraversableLike[_, R]](r: R)(implicit fr: IsTraversableLike[R]): TailOption[fr.A, R] =
  //  new TailOption(r)
}