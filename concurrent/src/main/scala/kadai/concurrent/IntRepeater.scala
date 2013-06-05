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
package concurrent

import language.implicitConversions
import log.Logging

object IntRepeater {
  implicit def AddRepeatSyntax(i: Int) = new IntRepeater(i)
}

class IntRepeater(i: Int) extends Logging {
  import Logging._

  require(i > 0, "Repeat amount must be > 0")

  def times[A](x: => A) = {
    @annotation.tailrec
    def loop(last: A)(left: Int): A =
      if (left <= 0) last
      else loop(x)(left - 1)
    loop(x)(i - 1)
  }

  /**
   * Try executing a function n times.
   * This ignores exceptions until the last try.
   */
  def retries[A](f: => A): A =
    retriesWith { _ => () }(f)

  /**
   * Try executing a function n times, executing a backoff strategy in between.
   * This ignores exceptions until the last try.
   */
  def retriesWith[A](backoff: Int => Unit)(f: => A): A = {
    // compiler doesn't optimise tail recursion in catch clauses, hence the workaround using Option
    @annotation.tailrec
    def loop(t: Int): A = {
      try Some(f)
      catch {
        case e: Exception if t <= i =>
          withLog(s"retry-operation failed: ${e} attempt $t of max $i") {
            None
          }
      }
    } match {
      case Some(a) => a
      case None    => backoff(t); loop(t + 1)
    }
    loop(1)
  }
}
