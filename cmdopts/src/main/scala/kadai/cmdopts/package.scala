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
import scalaz._
import Scalaz._

package object cmdopts {

  implicit class ValidationNelSyntax[A, B](val v: ValidationNel[A, B]) extends AnyVal {
    def &&[C](other: ValidationNel[A, C]): ValidationNel[A, (B, C)] = (v |@| other) tupled
  }
}
