package kadai
package hash

import scalaz.{ @@, Tag }

trait HashTypes {

  type Hash[A] = String @@ A

  sealed trait Base16
  object Base16 extends Hasher[Base16](Encoding.B16.contains)

  sealed trait Base32
  object Base32 extends Hasher[Base32](Encoding.B32.contains)

  private[HashTypes] sealed class Hasher[A](pred: Char => Boolean) {
    def apply(s: String): Option[Hash[A]] =
      if (s.forall { c => pred(c.toUpper) }) Some(Tag(s))
      else None
  }
}