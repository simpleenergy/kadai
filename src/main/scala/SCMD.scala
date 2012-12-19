package io.kadai

import scala.language.implicitConversions
import shapeless._
import FromTraversable._
import Traversables._
import Nat._
import Tuples._
import scalaz._
import Scalaz._

abstract class CmdOpts[T](rawdata: Seq[T]) {

  implicit def opt[R]( s: T, f: () => R ): Option[R] = rawdata.find(_==s).map( _ => f() )
  implicit def opt[R,P <: Product]( s: T, f: P => R )(implicit p: Producer[R,P]): Option[R] = for {
    tl <- tailfind(s)
    ret <- p(f,tl)
  } yield ret

  def tailfind( s: T ): Option[Seq[T]] = {
    // Oh scala your lack of tailOption is pathetic
    val tl = try { rawdata.dropWhile( _ != s ).tail }
             catch { case _: Throwable => Nil }
    tl match {
      case Nil => None
      case ret => Some(ret)
    }
  }

  trait Producer[R, P <: Product] {
    type Out = Option[R]
    def apply( f: P => R, as: Seq[T] ): Out
  }

  object Producer {
    implicit def getmeone[R, P <: Product, N <: Nat, H <: HList]
        ( implicit tp: TuplerAux[H, P], hl: LengthAux[H, N], toHL: FromTraversable[H],
                   allA: LUBConstraint[H, T], toI: ToInt[N]) = new Producer[R,P] {
        def apply( f: P => R, as: Seq[T] ) = for {
          hl <- toHL(as.take(toI()))
        } yield f(hl.tupled)
    }
  }

  def usage: Option[String] = None
  def version: Option[String] = None
  def handle_info() { sys.exit() }

  // Convenience methods, TRUE and FALSE are suprisingly common
  val TRUE = () => true
  val FALSE = () => false

  // Call all the startup code
  val startup = {
    val a = for { vs <- version; if !vs.isEmpty } yield println("Version: "+vs)
    val b = for { us <- usage; if !us.isEmpty } yield println("Usage: "+us)
    val aorb = for{ x <- a.orElse(b) } yield handle_info
    // TODO validation
  }
}

object CMDExample {
  object CFG extends CmdOpts( List( "--name", "bob", "baz" ) ) {
    // NB: lazy val effectively memoizes the result
    lazy val name = opt("--name", (x: (String, String)) => "%s and %s".format(x._1, x._2))
    lazy val all = opt("--all", TRUE)
    lazy val absent = opt("--absent", TRUE)
    override def version = opt("--version",() => "10.1.5")
    override def usage = opt("--help",() => "Some random help text here")
  }
  println(CFG.name)
  println("ALL: "+CFG.all)
  println(CFG.absent)
}
