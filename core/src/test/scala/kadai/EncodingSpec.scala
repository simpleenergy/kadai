package kadai

import org.specs2.Specification
import scalaz.syntax.either._

class EncodingSpec extends Specification {
  import Encoding._
  import Result._

  def is = s2"""
  Encoding should
    safely encode base16 constants                                     $encodeBase16Constants
    safely encode base32 constants                                     $encodeBase32Constants
    parse base16 strings                                               $parseBase16
    parse base32 strings                                               $parseBase32
    base16 parse errors                                                $parseBase16Error
    base32 parse errors                                                $parseBase32Error
  """

  def encodeBase16Constants =
    b16"00" === 0 and
      b16"10" === 16 and
      b16"aa" === 170 and
      b16"7110eda4d09e062aa5e4a390b0a572ac0d2c0220" === BigInt("645493470721644289521265973501766936213572026912")

  def encodeBase32Constants =
    b32"AA" === 0 and
      b32"BA" === 32 and
      b32"33G" === 28518 and
      b32"G6VGHR3TTDMVIRZSMLQ2ABL4DZRS5WTX" === BigInt("317794311065214497895902842099257310940129843831")

  def parseBase16 =
    B16.toBigInt("7110eda4d09e062aa5e4a390b0a572ac0d2c0220") must be equalTo BigInt("645493470721644289521265973501766936213572026912").right

  def parseBase32 =
    B32.toBigInt("G6VGHR3TTDMVIRZSMLQ2ABL4DZRS5WTX") must be equalTo BigInt("317794311065214497895902842099257310940129843831").right

  def parseBase16Error =
    B16.toBigInt("fred") must be equalTo "'fred' contains characters not in Vector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, A, B, C, D, E, F)".invalidResult

  def parseBase32Error =
    B32.toBigInt("oh noes!") must be equalTo "'oh noes!' contains characters not in Vector(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, 2, 3, 4, 5, 6, 7)".invalidResult
}
