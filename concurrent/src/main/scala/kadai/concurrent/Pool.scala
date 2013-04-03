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

object Pool {
  val empty = Pool[Nothing]()
}

/**
 * Pool manages two queues of available and busy objects.
 * 
 * Immutable, variable reference needs to be managed externally.
 */
case class Pool[+A](available: List[A] = Nil, busy: List[A] = Nil) {

  def ::[B >: A](a: B) = add(a)

  def -:[B >: A](a: B) = remove(a)

  def add[B >: A](a: B) =
    copy(available = a :: available)

  def remove[B >: A](a: B) =
    copy(available = available.filterNot(_ == a), busy = busy.filterNot(_ == a))

  def borrow: (Pool[A], Option[A]) =
    available.headOption match {
      case None        => this -> None
      case s @ Some(a) => copy(available = available.tail, busy = a :: busy) -> s
    }

  def borrowWhile(f: A => Boolean): (Pool[A], Option[Seq[A]]) =
    available span f match {
      case (Nil, _)     => this -> None
      case (as, remain) => copy(available = remain, busy = as ::: busy) -> Some(as)
    }

  def giveBack[B >: A](a: B): Pool[B] =
    copy(available = a :: available, busy = busy.filterNot(_ == a))
}

/**
 * Creates a thread-safe mutable Pool wrapper
 */
object AtomicPool {
  def apply[A](available: List[A] = Nil, busy: List[A] = Nil): Atomic[Pool[A]] = Atomic(Pool(available, busy))
}