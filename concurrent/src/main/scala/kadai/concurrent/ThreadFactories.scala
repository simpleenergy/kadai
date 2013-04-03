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

import java.lang.Thread.UncaughtExceptionHandler
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.ThreadFactory
import scalaz.syntax.id._

/**
 * Factory for creating {@link ThreadFactory} instances. All factory
 * implementations produce named threads to give good stack-traces.
 */
object ThreadFactories {

  sealed class Type(val isDaemon: Boolean)
  object DAEMON extends Type(true)
  object USER extends Type(false)

  /**
   * Simple builder for {@link ThreadFactory} instances
   */
  case class Builder(
    name: String,
    threadType: Type = USER,
    priority: Int = Thread.NORM_PRIORITY,
    handler: UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()) {

    def build: ThreadFactory =
      Default(name, threadType, priority, handler)

    def isDaemon = copy(threadType = DAEMON)
  }

  def named(name: String) = new Builder(name)

  object Default {
    def apply(name: String, threadType: Type, priority: Int, handler: UncaughtExceptionHandler): ThreadFactory =
      new Default(
        new ThreadGroup(
          Option(System.getSecurityManager).map {
            _.getThreadGroup
          } getOrElse { Thread.currentThread.getThreadGroup }
        , name),
        name + ":thread-", threadType, priority, handler)
  }

  class Default(group: ThreadGroup, prefix: String, threadType: Type, priority: Int, handler: UncaughtExceptionHandler) extends ThreadFactory {
    require(priority >= Thread.MIN_PRIORITY, "priority too low")
    require(priority <= Thread.MAX_PRIORITY, "priority too high")

    val nextId = new SequenceGenerator(1)

    def newThread(r: Runnable) =
      new Thread(group, r, prefix + nextId(), 0) <| { t =>
        t.setDaemon(threadType.isDaemon)
        t.setPriority(priority)
        t.setUncaughtExceptionHandler(handler)
      }
  }
}