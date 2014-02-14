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

import Throwables.ShowThrowable
import scalaz.{ Cord, Equal, Monoid, NonEmptyList, Show, \/ }
import scalaz.Scalaz._

sealed trait Invalid

object Invalid {

  case class Message(s: String) extends Invalid
  case class Err(x: Throwable) extends Invalid {
    // need to implement custom equality as Throwable does not define equals
    override def equals(that: Any) = that match {
      case Err(t) => (t.getClass == x.getClass) && (t.getMessage == x.getMessage)
      case _      => false
    }
    override def hashCode = x.getClass.hashCode + Option(x.getMessage).hashCode
  }
  case class Composite(is: NonEmptyList[Invalid]) extends Invalid
  object Zero extends Invalid

  trait ConvertTo {
    def invalid: Invalid
    final def invalidResult[A]: Invalid \/ A = invalid.left
    final def invalidNel[A]: NonEmptyList[Invalid] = invalid.wrapNel
  }

  implicit object ShowInvalid extends Show[Invalid] {
    val newline = Cord(System getProperty "line.separator")
    override def show(inv: Invalid) =
      inv match {
        case Message(m)   => m.show
        case Err(e)       => e.show
        case Composite(l) => l.show
        case Zero         => "unknown".show
      }
  }

  implicit object EqualInvalid extends Equal[Invalid] {
    def equal(a: Invalid, b: Invalid) = a == b
  }

  implicit object InvalidMonoid extends Monoid[Invalid] {
    def zero = Zero
    def append(a1: Invalid, a2: => Invalid) =
      (a1, a2) match {
        case (Zero, Zero)                   => Zero
        case (l, Zero)                      => l
        case (Zero, r)                      => r
        case (Composite(as), Composite(bs)) => Composite(as |+| bs)
        case (Composite(ls), r)             => Composite(ls |+| NonEmptyList(r))
        case (l, Composite(rs))             => Composite(NonEmptyList(l) |+| rs)
        case (l, r)                         => Composite(NonEmptyList(l, r))
      }
  }
}