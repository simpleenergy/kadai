package io

package object kadai {

  import collection.GenTraversableLike
  import collection.generic.IsTraversableLike

  implicit class TraversableOptionalSyntax[Repr <: GenTraversableLike[_, Repr]](val rep: Repr) extends AnyVal {
    //type A = ???
    def notEmpty: Option[Repr] =
      if (rep.isEmpty) None else Some(rep)

    def tailOption: Option[Repr] =
      notEmpty flatMap { r => new TraversableOptionalSyntax(r.tail).notEmpty }

    // experiment, needs to be: (A, Repr) currently: (Any, Repr)
    def headTailOption: Option[(Any, Repr)] =
       notEmpty map { r => r.head -> r.tail }
  }

  // somehow we need to capture IsTraversableLike's head type
  //implicit def ToTailOption[A, R<: GenTraversableLike[_, R]](r: R)(implicit fr: IsTraversableLike[R]): TailOption[fr.A, R] =
  //  new TailOption(r)
}