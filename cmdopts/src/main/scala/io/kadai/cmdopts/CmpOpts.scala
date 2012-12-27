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
package io.kadai
package cmdopts

import shapeless._

abstract class CmdOpts[T](rawdata: Seq[T]) {

  def opt[H <: HList, N <: Nat, F, R](t: T, f: F)(
    implicit hlister: FnHListerAux[F, H => R],
    length: LengthAux[H, N],
    toHList: FromTraversable[H],
    size: ToInt[N]): Option[R] =
    for {
      ts <- if (size() > 0) tailfind(t) else rawdata.find { _ == t }.map { _ => Nil }
      hlist <- toHList(ts.take(size()))
    } yield hlister(f)(hlist)

  private def tailfind(t: T): Option[Seq[T]] =
    rawdata.dropWhile(_ != t).tailOption

  protected def usage: Option[String] = None
  protected def version: Option[String] = None
  protected def handleInfo() { sys.exit() }

  // Call all the startup code
  val startup = {
    val a = for { vs <- version; if !vs.isEmpty } yield println("Version: " + vs)
    val b = for { us <- usage; if !us.isEmpty } yield println("Usage: " + us)
    val aorb = for { x <- a orElse b } yield handleInfo
    // TODO validation
  }
}

object CmdOpts {
  // Convenience methods, TRUE and FALSE are suprisingly common
  val TRUE = () => true
  val FALSE = () => false
}