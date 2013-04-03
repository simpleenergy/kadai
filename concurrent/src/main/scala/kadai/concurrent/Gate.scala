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
package kadai.concurrent

import java.util.concurrent.{ TimeUnit, CountDownLatch }
import util.control.NonFatal

object Gate {
  def await(latch: CountDownLatch) {
    if (!latch.await(1, TimeUnit.MINUTES))
      throw new TimedOutException
  }

  class TimedOutException extends IllegalStateException
}

class Gate(threads: Int) {
  private[this] val _ready = new CountDownLatch(threads);
  private[this] val _start = new CountDownLatch(1);

  /** wait until the right number of things come in then let them go
    */
  def apply[A, B](a: => A)(f: A => B): B = {
    val param =
      try a
      catch {
        case NonFatal(e) =>
          ready()
          throw e
      }

    ready()
    f(param)
  }

  import Gate._

  /** Called from the racing threads when ready. They will then block until all threads are at this point. */
  def ready() {
    _ready.countDown
    await(_start)
  }

  /** Called from the starter thread. Blocks until everybody is ready, and then signals go. */
  def go() {
    await(_ready)
    _start.countDown
  }
} 
