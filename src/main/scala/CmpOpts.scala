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

abstract class CmdOpts[T](rawdata: Seq[T]) {

  def opt[H <: HList,N <: Nat,F,R](s: T, f: F)
      ( implicit toHLF: FnHListerAux[F,H => R], hlen: LengthAux[H,N]
                ,toHL: FromTraversable[H], allT: LUBConstraint[H,T]
                ,toI: ToInt[N]) = for {
    tl <- if(toI() > 0) tailfind(s) else rawdata.find(_ == s).map(_ => Nil)
    ahl <- toHL(tl.take(toI()))
  } yield toHLF(f)(ahl)

  private def tailfind(s: T): Option[Seq[T]] = {
    // l.tailOption would be very nice here...
    val tl =
      try { rawdata.dropWhile(_ != s).tail }
      catch { case _: Throwable => Nil }

    tl match {
      case Nil => None
      case ret => Some(ret)
    }
  }

  protected def usage: Option[String] = None
  protected def version: Option[String] = None
  protected def handle_info() { sys.exit() }

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
