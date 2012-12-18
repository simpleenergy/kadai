package com.test1

import scala.language.implicitConversions
import shapeless._
import FromTraversable._
import Traversables._
import Nat._
import Tuples._
import scalaz._
import Scalaz._

abstract class CMDS[T](rawdata: Seq[T]) {

  implicit def opt[R]( s: T, f: () => R ): Option[R] = rawdata.find(_==s).map( _ => f() )
  implicit def opt[R,P <: Product]( s: T, f: P => R )(implicit p: Producer[R,P]): Option[R] = tailfind(s).map(p(f,_))

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
    type Out = R
    def apply( f: P => R, as: Seq[T] ): Out
  }

  object Producer {
    implicit def getmeone[R, P <: Product, N <: Nat, H <: HList]
        ( implicit tp: TuplerAux[H, P], hl: LengthAux[H, N], toHL: FromTraversable[H],
                   allA: LUBConstraint[H, T], toI: ToInt[N]) = new Producer[R,P] {
        def apply( f: P => R, as: Seq[T] ) = f((toHL(as.take(toI())).get).tupled)
    }
  }

  def usage: Option[String] = None
  def version: Option[String] = None
  def handle_info = sys.exit()

  // Convenience methods, TRUE and FALSE are suprisingly common
  val TRUE = () => true
  val FALSE = () => false

  // Call all the startup code
  val startup = {
    version.map(x=>if(!x.isEmpty) println("Version: "+x) else ())
    usage.map(x=>if(!x.isEmpty) println("Usage: "+x) else ())
    // "handle" whether or not we exit
    //handle_info //WHY DOES THIS CAUSE A NON-0 EXIT?
  }
}

object Test extends App {
  object CFG extends CMDS( args ) {
    lazy val name = opt("--name", (x: (String, String)) => { println("bang"); "%s and %s".format(x._1, x._2) })
    lazy val all = opt("--all", TRUE)
    lazy val absent = opt("--absent", TRUE)
    override def version = opt("--version",() => "10.1.5")
    override def usage = opt("--help",() => "Some random help text here")
  }
//  println(CFG.name)
//  println(CFG.name)
  println("ALL: "+CFG.all)
  println("ALL: "+CFG.all)
  println(CFG.absent)
}
