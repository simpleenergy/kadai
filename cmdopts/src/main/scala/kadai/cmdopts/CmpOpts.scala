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
package cmdopts

import scala.util.control.Exception.nonFatalCatch

import scalaz.{ NonEmptyList, ValidationNel }

import scalaz.Scalaz._
//import scalaz.syntax.std.option._
//import scalaz.syntax.validation._
import shapeless.{ HList, Nat }
import shapeless.ops.function.FnToProduct
import shapeless.ops.hlist.Length
import shapeless.ops.nat.ToInt
import shapeless.ops.traversable.FromTraversable

/**
 * This class implements a simple but type-safe command-line option parser.
 *
 * You will generally use this by declaring an object that extends CmdOpts and passing in
 * args (the array of cmd-line String arguments).
 *
 * You then ask for the specific options by calling 'opt' with the String for the option name,
 * and a function that takes the next n Strings and returns a thing of the type you want. If
 * the arguments are found they are passed to the function and the result is returned inside
 * a Some, if they cannot be found or an exception is thrown from the function, a None is returned.
 *
 * Validation error messages can be specified by implementing the validate method.
 *
 * Usage and Version messages can be specified by implementing the usage and version methods respectively.
 *
 * @see Example.scala
 */
abstract class CmdOpts[T](rawdata: Seq[T]) {

  /**
   * Find the supplied command-line option, if given.
   *
   * Note: will require `import kadai.cmdopts.nat._` to get the Nat instances it requires.
   *
   * @param the option to look for
   * @param a function of some variable arity (all of type T) that will take the number of Ts
   * supplied and convert into whatever the desired output type is.
   */
  def opt[H <: HList, N <: Nat, F, R](t: T, f: F)(
    implicit hlister: FnToProduct.Aux[F, H => R],
    length: Length.Aux[H, N],
    toHList: FromTraversable[H],
    size: ToInt[N]): Option[R] =
    for {
      ts <- if (size() > 0) tailfind(t) else rawdata.find { _ == t }.map { _ => Nil }
      hl <- toHList(ts.take(size()))
      op <- nonFatalCatch.opt { hlister(f)(hl) }
    } yield op

  def check[R](xopt: Option[R], err: String): ValidationNel[String, R] =
    xopt.map(_.successNel[String]).getOrElse(err.failNel[R])

  protected def usage: Option[String] =
    None

  protected def version: Option[String] =
    None

  protected def validate: ValidationNel[String, _] =
    ().successNel[String]

  protected def handleInfo() {
    sys.exit()
  }

  protected def handleErrors(xs: NonEmptyList[String]) {
    xs.foreach(println)
  }

  // Call all the startup code
  val startup = {
    val valid = validate.fold(handleErrors(_).some, _ => None)
    val ver = for { vs <- version; if !vs.isEmpty } yield println("Version: " + vs)
    val use = for { us <- usage; if !us.isEmpty } yield println("Usage: " + us)
    for {
      _ <- valid orElse ver orElse use
    } yield handleInfo
  }

  private def tailfind(t: T): Option[Seq[T]] =
    rawdata.dropWhile(_ != t).tailOption
}

object CmdOpts {
  // Convenience methods, TRUE and FALSE are suprisingly common
  val TRUE = () => true
  val FALSE = () => false
}
