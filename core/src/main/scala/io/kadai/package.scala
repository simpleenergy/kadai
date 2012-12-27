/* 
 * Copyright 2012 Atlassian PTY LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

  implicit class SideEffectReturningSyntax[A](val a: A) extends AnyVal {
    def ~~(effect: A => Any): A = { effect(a) ; a }
  }
  
  // somehow we need to capture IsTraversableLike's head type
  //implicit def ToTailOption[A, R<: GenTraversableLike[_, R]](r: R)(implicit fr: IsTraversableLike[R]): TailOption[fr.A, R] =
  //  new TailOption(r)
}