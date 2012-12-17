package com.test1

import scala.language.implicitConversions
import shapeless._
import FromTraversable._
import Traversables._
import Nat._
import Tuples._

// Maybe a way of getting to a ProductArity,Nat from ProductN type
trait ToArity[P, N <: Nat]
object ToArity {
  implicit def prod1[P <: Product1[_]] = new ToArity[P, _1] {}
  implicit def prod2[P <: Product2[_, _]] = new ToArity[P, _2] {}
  implicit def tuple2: ToArity[(String, String), _2] = new ToArity[(String, String), _2] {}
  // ad nauseum...
}

// Define a uniform HList type from type S
trait SizedHList[A, N <: Nat] {
  type Out <: HList
}

trait SizedHListAux[A, N <: Nat, T <: HList]

object SizedHList {
  implicit def make[A, N <: Nat, T <: HList](implicit k: SizedHListAux[A, N, T]) = new SizedHList[A, N] {
    type Out = T
  }
}

object SizedHListAux {
  implicit def base[A, H <: HList] = new SizedHListAux[A, _0, HNil] {}
  implicit def induct[A, H <: HList, N <: Nat, P <: Nat, R <: PredAux[N, P]](implicit r: R, k: SizedHListAux[A, P, H]) = new SizedHListAux[A, N, A :: H] {}
}

trait SomeFun {
  type Result
  def apply(): Result
}

//  // This works but has fixed arity of the Product type of Function1's argument
//  object SomeFun {
//    implicit def fromF1[T]( f1: (Function1[(String,String),T],List[String]) ) = new SomeFun {
//      type Result = (T,List[String])
//      def apply(): Result = {
//        val (ts,rest) = (f1._2.take(2),f1._2.drop(2))
//        (f1._1(ts.toHList[String::String::HNil].get.tupled),rest)
//      }
//    }
//  }

// But I want to abstract over S, the contained type in the List
// over P the Product type which is the arg notably its arity
// This means we need to recover arity of the Product type and render it in value space
// and also means that we need to compute the type of the intermediate HList
object SomeFun {
  def produce(m: SomeFun): m.Result = m()

  // TODO implement fromF0/Function0
  // TODO fix the tupliness of f1 to better take two args...?
  implicit def fromF1[T, A, P <: Product, N <: Nat, H <: HList](f1: (P => T, List[A]))(implicit k: ToArity[P, N], toI: ToInt[N], l: SizedHListAux[A, N, H], toHL: FromTraversable[H], tp: TuplerAux[H, P]) =
    new SomeFun {
      type Result = (T, List[A])
      def apply(): Result = {
        val (f, as) = f1
        val (ts, rest) = (as.take(toI()), as.drop(toI()))
        f((toHL(ts).get).tupled) -> rest
      }
    }
  // Debug Arity checker
  def printArity[P <: Product, N <: Nat](p: P)(implicit k: ToArity[P, N], toI: ToInt[N]) = println("Arity: " + toI())
}

object Test {
  val thedata = List("foo", "bar", "baz", "bob")
  val tfn = (x: (String, String)) => println("%s and %s".format(x._1, x._2))
  def foo = SomeFun.printArity("a" -> "b")
  //def doit = SomeFun.produce((tfn, thedata))
}