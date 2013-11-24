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
package log

import scalaz._
import scalaz.Scalaz._
import Logging.stringInstance
import org.apache.logging.log4j.ThreadContext.pop
import org.apache.logging.log4j.ThreadContext.push

/**
 * Defines a simple logging facility.
 *
 * To use simply mix in the Logging trait.
 * The standard Show type-class instances can
 * be imported by importing the Logging object:
 *
 * For example:
 * {{{
 *
 * class MyClass extends Actor with Logging {
 * import Logging._
 *
 * info("MyClass is instantiated!)
 * …
 * }
 * }}}
 */
trait Logger {
  protected def error[A: Show](msg: => A): Unit
  protected def warn[A: Show](msg: => A): Unit
  protected def info[A: Show](msg: => A): Unit
  protected def debug[A: Show](msg: => A): Unit
  protected def trace[A: Show](s: => A): Unit
}

trait LoggingInstances extends std.AllInstances {
  private[log] def apply(cls: Class[_]) = org.apache.logging.log4j.LogManager.getLogger(cls)

  implicit def ShowStringSeq = new Show[Seq[String]] {
    override def shows(ts: Seq[String]) = ts.toList.shows
  }
}

object Logging extends LoggingInstances

trait Logging extends Logger {
  import Logging._
  /** allow syntax: log info "message" */
  @transient
  protected final val log = Logging(this.getClass)

  private def show[A: Show](msg: => A) =
    implicitly[Show[A]].shows(msg)

  override protected def error[A: Show](msg: => A) =
    log.error { show(msg) }

  override protected def warn[A: Show](msg: => A) =
    if (log.isWarnEnabled)
      log.warn { show(msg) }

  override protected def info[A: Show](msg: => A) =
    if (log.isInfoEnabled)
      log.info { show(msg) }

  override protected def debug[A: Show](msg: => A) =
    if (log.isDebugEnabled)
      log.debug { show(msg) }

  override protected def trace[A: Show](msg: => A) =
    if (log.isTraceEnabled)
      log.trace { show(msg) }

  def withLog[A](s: String)(f: => A): A = {
    info(s)
    f
  }

  def withLogContext[A](s: String)(f: => A): A = {
    import org.apache.logging.log4j.ThreadContext._
    push(s)
    try f finally { pop; () }
  }
}
