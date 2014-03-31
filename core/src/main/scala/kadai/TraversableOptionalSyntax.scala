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
package kadai

import collection.GenTraversableLike
import collection.generic.IsTraversableLike
import scalaz.std.option._
import scalaz.syntax.id._
import scalaz.syntax.std.boolean._
import scalaz.syntax.std.option._

/**
 * Add safe, total methods such as tailOption to anything Traversable
 */
trait TraversableOptional {

  /**
   * Add syntax:
   *
   * {{{
   * coll.notEmpty: Option[CollType]
   * }}}
   */
  implicit class NotEmptySyntax[A, Repr: IsTraversableLike](rep: Repr) {
    def notEmpty: Option[Repr] =
      implicitly[IsTraversableLike[Repr]].conversion(rep).isEmpty ?
        none[Repr] | rep.some
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
    private def opt[A](thunk: => A): Option[A] =
      if (rep.isEmpty) none[A] else thunk.some

    def tailOption: Option[Repr] =
      opt(rep.tail)

    def headTailOption: Option[(A, Repr)] =
      opt(rep.head -> rep.tail)

    /** @since 1.4 */
    def initOption: Option[Repr] =
      opt(rep.init)

    /** @since 1.4 */
    def initLastOption: Option[(Repr, A)] =
      opt(rep.init -> rep.last)
  }

  implicit def AddTraversableOptionalSyntax[A, Repr](rep: Repr)(implicit fr: IsTraversableLike[Repr]): TraversableOptionalSyntax[fr.A, Repr] =
    new TraversableOptionalSyntax(fr conversion rep)
}
