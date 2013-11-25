package kadai

import scala.reflect.macros.Context
import Encoders._

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

    def toBigIntUnsafe(s: String): BigInt =
      if (s.forall { contains })
        s.map { c => indexOf(c.toUpper) }.foldLeft(0: BigInt) { (a, b) => a * base + b }
      else
        throw new NumberFormatException(s"'$s' contains characters not in $chars")

    def toBigInt(s: String): Result[BigInt] =
      Result.catchingToResult { toBigIntUnsafe(s) }
  }

  implicit class Base16EncodingMacro(sc: StringContext) {
    def b16(): BigInt = macro Base16Macro.encode
  }

  implicit class Base32EncodingMacro(sc: StringContext) {
    def b32(): BigInt = macro Base32Macro.encode
  }
}
