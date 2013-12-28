package kadai

import org.specs2.{ ScalaCheck, Specification }
import org.scalacheck.Prop

class TraversableOptionalSpec extends Specification with ScalaCheck {
  def is = s2"""
  TraversableOptionalSyntax should
    add .tailOption                          $addTailOption
    add .headTailOption                      $addHeadTailOption
    add .notEmpty                            $addNotEmpty
  """

  def addTailOption = Prop.forAll { ls: List[String] =>
    (ls, ls.tailOption) must beLike {
      case (List(), None)      => ok
      case (_ :: as, Some(bs)) => as === bs
    }
  }

  def addHeadTailOption = Prop.forAll { ls: List[String] =>
    (ls, ls.headTailOption) must beLike {
      case (Nil, None)              => ok
      case (a :: as, Some((b, bs))) => (a === b) and (as === bs)
    }
  }

  val array = Array(2)

  def addNotEmpty =
    List[String]().notEmpty === None and
      List(1).notEmpty === Some(List(1)) and
      Vector[String]().notEmpty === None and
      Vector(3).notEmpty === Some(Vector(3)) and
      Array[String]().notEmpty === None and
      array.notEmpty === Some(array) and
      "".notEmpty === None and
      "fred".notEmpty === Some("fred")
}