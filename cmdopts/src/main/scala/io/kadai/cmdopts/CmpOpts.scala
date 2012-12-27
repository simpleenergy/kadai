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
package cmd

import shapeless._
import scalaz._
import Scalaz._

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

  implicit def check[R](xopt: Option[R], err: String): ValidationNEL[String,R] =
    xopt.map(_.successNel[String]).getOrElse(err.failNel[R])

  private def tailfind(t: T): Option[Seq[T]] =
    rawdata.dropWhile(_ != t).tailOption

  protected def usage: Option[String] = None
  protected def version: Option[String] = None
  protected def handleInfo() { sys.exit() }
  protected def validate: ValidationNEL[String,_] = ().successNel[String]
  protected def handleErrors( xs: NonEmptyList[String] ) = { xs.foreach(println) }

  // Call all the startup code
  val startup = {
    val valid = validate.fold( es => handleErrors(es).some , _ => None )
    val ver = for { vs <- version; if !vs.isEmpty } yield println("Version: " + vs)
    val use = for { us <- usage; if !us.isEmpty } yield println("Usage: " + us)
    for { x <- valid orElse ver orElse use } yield handleInfo
  }
}

object CmdOpts {
  // Convenience methods, TRUE and FALSE are suprisingly common
  val TRUE = () => true
  val FALSE = () => false
}
