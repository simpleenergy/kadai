/* 
 * Copyright 2013 Atlassian PTY LTD
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

import scalaz.{ \/, NonEmptyList }
import scalaz.syntax.id._
import scala.util.control.NonFatal

trait ResultInstances {

  implicit def toAddInvalidToString(s: String) = new AddInvalidToString(s)
  class AddInvalidToString(s: String) extends Invalid.ConvertTo {
    override def invalid: Invalid = Invalid.Message(s)
  }

  implicit def toAddInvalidToThrowable(x: Throwable) = new AddInvalidToThrowable(x)
  class AddInvalidToThrowable(x: Throwable) extends Invalid.ConvertTo {
    override def invalid: Invalid = Invalid.Err(x)
  }

  /** Evaluate the given value, which might throw an exception. */
  def catchingToResult[A](a: => A): Result[A] =
    try a.right
    catch {
      case NonFatal(e) => e.invalidResult
    }

}
