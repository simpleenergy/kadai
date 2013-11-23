package kadai
package hash

import scalaz.{ Tag, syntax }
import kadai.Encoding
import syntax.id._

case class SHA1(val bigInt: BigInt) {
  import SHA1._

  lazy val toBase16: Hash[Base16] =
    Tag(toString(16).padLeft(40, '0'))

  lazy val toBase32: Hash[Base32] =
    Tag(new String(toString(32).map { _.asDigit }.map { Encoding.B32 }.toArray).padLeft(32, 'A'))

  def toByteArray: Array[Byte] = {
    val b = bigInt.toByteArray
    // BigInt.toByteArray adds a zero byte to the head of the byte array when the
    // first bit is a '1'.  The first bit is a sign bit.  We strip this byte.
    // ie we turn it back into two's complement
    if (b(0) == 0) b.tail
    else b
  }

  def toString(base: Int) =
    bigInt.toString(base)

  override def toString =
    s"SHA1(${toBase16})"
}

/** Tag for computed SHA1 Strings */
object SHA1 { //extends (String => SHA1) {

  def fromBase32(base32: Hash[Base32]): SHA1 =
    from(base32, 32) { Encoding.B32.indexOf(_) }

  def fromBase16(base16: Hash[Base16]): SHA1 =
    from(base16, 16) { Encoding.B16.indexOf(_) }

  private def from(s: String, base: Int)(f: Char => Int): SHA1 =
    SHA1 { s.map { c => f(c.toUpper) }.foldLeft(0: BigInt) { (a, b) => a * base + b } }

  def hash(text: String): SHA1 =
    hashOf(text.getBytes(charset))

  def hashOf(bytes: Array[Byte]): SHA1 =
    Digester.compute(Digester(bytes))

  /** ADT that has a Monoid for computing a SHA1 */
  sealed trait Digester

  object Digester extends (Array[Byte] => Digester) {
    def apply(bytes: Array[Byte]): Digester =
      Data(Vector(bytes))

    def compute(d: Digester): SHA1 = d match {
      case c @ Consumer() => c.digest
      case Data(bytes)    => (Consumer() + bytes).digest
    }

    implicit object DigesterMonoidHack extends scalaz.Monoid[Digester] {
      /** this one contains the MessageDigest */
      override def zero =
        Consumer()

      override def append(f1: Digester, f2: => Digester) = (f1, f2) match {
        case (Consumer(), Consumer())      => throw new UnsupportedOperationException("cannot reduce two consumers!")
        case (c @ Consumer(), Data(bytes)) => c + bytes
        case (Data(bytes), c @ Consumer()) => c + bytes
        case (Data(b1), Data(b2))          => Data(b1 ++ b2) // append the data
      }
    }
  }

  private[SHA1] case class Consumer() extends Digester {
    val md = java.security.MessageDigest getInstance "SHA-1"

    def +(bytes: Seq[Array[Byte]]): this.type = {
      for (b <- bytes) md.update(b)
      this
    }

    def digest: SHA1 =
      SHA1(BigInt(Array(0x00.toByte) ++ md.digest)) // digest is in two's complement
  }

  case class Data(bytes: Seq[Array[Byte]]) extends Digester
}
