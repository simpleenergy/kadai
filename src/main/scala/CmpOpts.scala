// Copyright 2012 Atlassian PTY LTD
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package io.kadai

import scala.language.implicitConversions
import scalaz.Scalaz._
import shapeless._
import FromTraversable._
import Nat._
import Traversables._
import Tuples._

abstract class CmdOpts[T](rawdata: Seq[T]) {

  implicit def opt[R](s: T, f: () => R): Option[R] = rawdata.find(_ == s).map(_ => f())
  implicit def opt[R, P <: Product](s: T, f: P => R)(implicit p: Producer[R, P]): Option[R] = for {
    tl <- tailfind(s)
    ret <- p(f, tl)
  } yield ret

  def tailfind(s: T): Option[Seq[T]] = {
    val tl =
      try { rawdata.dropWhile(_ != s).tail }
      catch { case _: Throwable => Nil }

    tl match {
      case Nil => None
      case ret => Some(ret)
    }
  }

  trait Producer[R, P <: Product] {
    type Out = Option[R]
    def apply(f: P => R, as: Seq[T]): Out
  }

  object Producer {
    implicit def Factory[R, P <: Product, N <: Nat, H <: HList](
      implicit tp: TuplerAux[H, P],
      hl: LengthAux[H, N],
      toHL: FromTraversable[H],
      allA: LUBConstraint[H, T],
      toI: ToInt[N]) = new Producer[R, P] {
      def apply(f: P => R, as: Seq[T]) = for {
        hl <- toHL(as.take(toI()))
      } yield f(hl.tupled)
    }
  }

  def usage: Option[String] = None
  def version: Option[String] = None
  def handle_info() { sys.exit() }

  // Convenience methods, TRUE and FALSE are suprisingly common
  val TRUE = () => true
  val FALSE = () => false

  // Call all the startup code
  val startup = {
    val a = for { vs <- version; if !vs.isEmpty } yield println("Version: " + vs)
    val b = for { us <- usage; if !us.isEmpty } yield println("Usage: " + us)
    val aorb = for { x <- a.orElse(b) } yield handle_info
    // TODO validation
  }
}
