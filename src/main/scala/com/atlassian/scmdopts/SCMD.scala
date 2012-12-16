package com.test1
import shapeless._
import FromTraversable._
import Traversables._
import Nat._
import Tuples._

object Test {
  // Maybe a way of getting to a ProductArity,Nat from ProductN type
  trait ToArity[P,N]
  object ToArity {
    implicit def prod1[P <: Product1[_]] = new ToArity[P,_1]{}
    implicit def prod2[P <: Product2[_,_]] = new ToArity[P,_2]{}
    // ad nauseum...
  }

  // Define a uniform HList type from type S
  trait SzHList[S,N <: Nat] {
    type Out <: HList
  }

  trait SzHListAux[S,N <: Nat,T <: HList]

  object SzHList {
    implicit def make[S,N <: Nat,T <: HList](implicit k: SzHListAux[S,N,T]) = new SzHList[S,N] {
      type Out = T
    }
  }

  object SzHListAux {
    implicit def base[S,T <: HList] = new SzHListAux[S,_0,HNil] {}
    implicit def ind[S,N <: Nat,T <: HList,P <: Nat,R <: PredAux[N,P]](implicit p: R, k: SzHListAux[S,P,T]) = new SzHListAux[S,N,S::T] {}
  }

  trait SomeFun {
    type Result
    def apply(): Result
  }

  def produce(m: SomeFun): m.Result = m()

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
    // TODO implement fromF0/Function0
    // TODO fix the tupliness of f1 to better take two args...?
    implicit def fromF1[T,S,P <: Product,N <: Nat,H <: HList]( f1: (Function1[P,T],List[S]) )(implicit k: ToArity[P,N], l:
SzHListAux[S,N,H], toI: ToInt[N], toHL: FromTraversable[H], tp: TuplerAux[H,P]) = new SomeFun {
      type Result = (T,List[S])
      def apply(): Result = {
        val (ts,rest) = (f1._2.take(toI()),f1._2.drop(toI()))
        (f1._1((toHL(ts).get).tupled),rest)
      }
    }
    // Debug Arity checker
    def printArity[P <: Product,N <: Nat]( p: P )( implicit k: ToArity[P,N], toI: ToInt[N] ) = println("Arity: "+toI())
    // Debug SzHList checker
//    def regularHList[P <: Product,N <: Nat,H <: HList]( p: P )( implicit k ToArity[P,N], toI: ToInt[N], s: SzListAux[String,N,H] ) = {
//      (0 until toI()).foldLeft[Hlist](....???? // No ideas here
//    }
  }

  val thedata = List("foo","bar","baz","bob")
  val tfn = (x: (String,String)) => println("%s and %s".format(x._1,x._2))
  //def doit = produce((tfn, thedata))
}
