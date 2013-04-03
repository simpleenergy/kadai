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

import java.util.concurrent.atomic.AtomicReference

/**
 * Extension to [[AtomicReference]] that allows more idiomatic usage. It adds a
 * "value" that can be set with an update function:
 * {{{
 * value = (a: A) => calculate(a)
 * value = calculate(_)
 * }}}
 */
final class Atomic[A <: AnyRef](default: A) extends AtomicReference[A](default) {
  /** Update from the old to a new value and return the newly computed value. */
  @annotation.tailrec
  final def update(f: A => A): A = {
    val a = get
    val b = f(a)
    if ((a.eq(get)) && compareAndSet(a, b)) 
      b
    else 
      update(f)
  }

  /** Update from the old to a new value and return a companion value computed at the same time. */
  @annotation.tailrec
  final def updateAndGet[B](f: A => (A, B)): B = {
    val old = get
    val (a, b) = f(old)
    if ((old.eq(get)) && compareAndSet(old, a)) 
      b
    else 
      updateAndGet(f)
  }

  /** Alias for get */
  def value = get

  /** Update method that allows the form: {{{value = updateFunction(_) }}} */
  def value_=(f: A => A): A = update(f)

  /** Get the current value as an Option */
  def option: Option[A] = Option(get)

  /** Atomically get the current value and set to null */
  def pop: Option[A] = Option(getAndSet(null.asInstanceOf[A]))
}

object Atomic {
  def apply[A <: AnyRef](a: A) = new Atomic(a)
}
