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

  class TraversableOptionalSyntax[A, Repr](rep: GenTraversableLike[A, Repr]) {
    def tailOption: Option[Repr] =
      if (rep.isEmpty) None
      else Some(rep.tail)

    def headTailOption: Option[(A, Repr)] =
      if (rep.isEmpty) None
      else Some(rep.head -> rep.tail)
  }

  implicit def TraversableOptionalSyntaxPimp[A, Repr](rep: Repr)(implicit fr: IsTraversableLike[Repr]): TraversableOptionalSyntax[fr.A, Repr] = 
    new TraversableOptionalSyntax(fr conversion rep)


  implicit class SideEffectReturningSyntax[A](val a: A) extends AnyVal {
    def ~~(effect: A => Any): A = { effect(a); a }
  }
}