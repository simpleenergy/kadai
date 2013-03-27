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
import scalaz.\/

package object kadai {

  type Result[A] = Invalid \/ A

  object Result extends ResultInstances

  //
  // add safe, total methods such as tailOption to traversables
  //

  import collection.GenTraversableLike
  import collection.generic.IsTraversableLike
  import scalaz._, Scalaz._
  import scalaz.syntax.id._

  /**
   * Add syntax:
   * 
   * {{{
   * coll.notEmpty: Option[CollType]
   * }}}
   */
  implicit class NotEmptySyntax[A, Repr: IsTraversableLike](rep: Repr) {
    def notEmpty: Option[Repr] =
      implicitly[IsTraversableLike[Repr]].conversion(rep).isEmpty ? none[Repr] | rep.some
  }

  /**
   * Add syntax:
   * 
   * {{{
   * coll.tailOption: Option[CollType]
   * coll.headTailOption: Option[(ElemType, CollType)]
   * }}}
   */
  class TraversableOptionalSyntax[A, Repr](rep: GenTraversableLike[A, Repr]) {
    def tailOption: Option[Repr] =
      rep.isEmpty ? none[Repr] | rep.tail.some

    def headTailOption: Option[(A, Repr)] =
      rep.isEmpty ? none[(A, Repr)] | (rep.head -> rep.tail).some
  }

  implicit def TraversableOptionalSyntaxPimp[A, Repr](rep: Repr)(implicit fr: IsTraversableLike[Repr]): TraversableOptionalSyntax[fr.A, Repr] =
    new TraversableOptionalSyntax(fr conversion rep)
}
