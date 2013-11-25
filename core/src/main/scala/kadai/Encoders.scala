package kadai

import scala.reflect.macros.Context
import scalaz.{ -\/, \/- }
import scala.reflect.api.Liftable

private[kadai] object Encoders {

  object Base16Macro {
    def encode(c: Context)(): c.Expr[BigInt] = MacroCommon.encodeImpl(c)(Encoding.B16.toBigInt, "Base16")
  }

  object Base32Macro {
    def encode(c: Context)(): c.Expr[BigInt] = MacroCommon.encodeImpl(c)(Encoding.B32.toBigInt, "Base32")
  }

  private[Encoders] object MacroCommon {
    def encodeImpl(c: Context)(parse: String => Result[BigInt], name: String): c.Expr[BigInt] = {
      import c.universe._

      c.Expr[BigInt] {
        c.prefix.tree match {
          case Apply(_, Apply(_, Literal(Constant(repr: String)) :: Nil) :: Nil) =>
            parse(repr) match {
              case \/-(b) =>
                q"BigInt(${b.toString})"
                //reify { i.splice.instance(s.splice).fold(_ => throw new RuntimeException, identity) }.tree
              case -\/(e) =>
                c.abort(c.enclosingPosition, "Invalid %s literal, parsing failed: %s".format(name, e))
            }
          case _ => c.abort(c.enclosingPosition, "Invalid %s literal.".format(name))
        }
      }
    }
  }
}