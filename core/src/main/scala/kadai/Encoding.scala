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

import reflect.macros.Context

import scalaz.syntax.id._
import scalaz.syntax.show._
import Result._

/**
 * Contains Base16 and Base32 (human-readable variant) encoding support, including as compile-time constants. eg:
 *
 * {{{
 * val a: BigInt = b16"ac60730edd01d21d3a367b638b5549c3b8fe2339"
 * val b: BigInt = b32"G6VGHR3TTDMVIRZSMLQ2ABL4DZRS5WTX"
 * }}}
 *
 * These will not compile if they contain incorrect characters for the encoding.
 */
object Encoding {
  object B16 extends Base(('0' to '9') ++ ('A' to 'F'), 16)
  object B32 extends Base(('A' to 'Z') ++ ('2' to '7'), 32)

  private[kadai] class Base(chars: IndexedSeq[Char], base: Int) extends (Int => Char) {
    def apply(i: Int) =
      chars(i)

    val contains: Char => Boolean =
      c => chars.contains(c.toUpper)

    val indexOf: Char => Int =
      chars.indexOf(_)

    /** Convert an encoded String to a BigInt on the right, or return an Invalid on the left if it doesn't parse correctly */
    def toBigInt(str: String): Result[BigInt] =
      if (str.forall { contains })
        str.map { c => indexOf(c.toUpper) }.foldLeft(0: BigInt) { (a, b) => a * base + b }.right
      else
        s"'$str' contains characters not in $chars".invalidResult

    /** Convert an encoded String to a BigInt, throwing an exception if it doesn't parse correctly */
    def toBigIntUnsafe(str: String): BigInt =
      toBigInt(str).fold(err => throw new NumberFormatException(err.shows), identity)
  }

  implicit class Base16EncodedBigIntConstant(sc: StringContext) {
    def b16(): BigInt = 
      macro Macro.Base16.encode
  }

  implicit class Base32EncodedBigIntConstant(sc: StringContext) {
    def b32(): BigInt = 
      macro Macro.Base32.encode
  }

  /**
   * Macro implementations
   */
  private[Encoding] object Macro {

    object Base16 {
      def encode(c: Context)(): c.Expr[BigInt] = 
        Common.impl(c)(Encoding.B16.toBigInt, "Base16")
    }

    object Base32 {
      def encode(c: Context)(): c.Expr[BigInt] = 
        Common.impl(c)(Encoding.B32.toBigInt, "Base32")
    }

    object Common {
      def impl(c: Context)(parse: String => Result[BigInt], name: String): c.Expr[BigInt] = {
        import c.universe._
        import scalaz.{ -\/, \/- }

        c.Expr[BigInt] {
          c.prefix.tree match {
            case Apply(_, Apply(_, Literal(Constant(repr: String)) :: Nil) :: Nil) =>
              parse(repr) match {
                case \/-(b) =>
                  q"BigInt(${b.toString})"
                case -\/(e) =>
                  c.abort(c.enclosingPosition, "Invalid %s literal, parsing failed: %s".format(name, e))
              }
            case _ => c.abort(c.enclosingPosition, "Invalid %s literal.".format(name))
          }
        }
      }
    }
  }
}
