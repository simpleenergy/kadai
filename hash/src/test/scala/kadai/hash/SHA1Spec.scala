package kadai
package hash

import org.specs2.Specification

class SHA1Spec extends Specification {
  def is = s2"""
  SHA1 must obey the following
  a base16 String must convert to the correct hash                                       $fromBase16
  a base32 String must convert to the correct hash                                       $fromBase32
  a SHA1 hash must output the correct base16 String                                      $toBase16
  a SHA1 hash must output the correct base32 String                                      $toBase32
  """

  import Encoding._

  def fromBase16 = 
    SHA1.fromBase16(Base16("7110eda4d09e062aa5e4a390b0a572ac0d2c0220").get) === SHA1.hash("1234")

  def fromBase32 = 
    SHA1.fromBase32(Base32("G6VGHR3TTDMVIRZSMLQ2ABL4DZRS5WTX").get) === SHA1.hash("some text")

  def toBase16 = 
    SHA1.hash("more text").toBase16 === "ac60730edd01d21d3a367b638b5549c3b8fe2339"

  def toBase32 = 
    SHA1.hash("some stuff").toBase32 === "F4VG2YYEE4N7CJQ2F7MOMHAG6NIGOZEU"
}