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

import scalaz.{ \/, Each, NonEmptyList }
import scalaz.syntax.id._
import scala.util.control.NonFatal

trait ResultInstances {

  implicit class AddInvalidToString(s: String) extends Invalid.ConvertTo {
    override def invalid: Invalid = Invalid.Message(s)
  }

  implicit class AddInvalidToThrowable(x: Throwable) extends Invalid.ConvertTo {
    override def invalid: Invalid = Invalid.Err(x)
  }

  implicit val EachResult = new Each[Result] {
    def each[A](fa: Result[A])(f: A => Unit) = fa map f
  }

  /** Evaluate the given value, which might throw an exception. */
  def catchingToResult[A](a: => A): Result[A] =
    try a.right
    catch {
      case NonFatal(e) => e.invalidResult
    }
}
