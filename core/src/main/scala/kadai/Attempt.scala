package kadai

import util.control.NonFatal
import scalaz.{ Equal, Isomorphism, Monad, Monoid, \/ }
import scalaz.syntax.id._

/**
 * Represents the result of an action, which may be the result itself or an Invalid (error)
 * @param run The result (either the result itself or Invalid)
 * @tparam A The type of the data to be returned from the action
 */
case class Attempt[+A](run: Invalid \/ A) {
  def map[B](f: A => B): Attempt[B] =
    Attempt(run map f)

  def flatMap[B](f: A => Attempt[B]): Attempt[B] =
    Attempt(run flatMap { a => f(a).run })
}

object Attempt {
  import Result._

  /** Puts the given strict value in Attempt. To run an operation safely, use safe() */
  def ok[A](value: A): Attempt[A] =
    Attempt(value.right)

  /** Puts the given error in an Attempt */
  def exception[A](t: Throwable): Attempt[A] =
    Attempt(t.invalidResult)

  /** Puts the given error in an Attempt */
  def fail[A](message: String): Attempt[A] =
    Attempt(message.invalidResult)

  /** Runs the given operation in try/catch. */
  def safe[A](op: => A): Attempt[A] =
    try ok(op)
    catch { case NonFatal(t) => exception(t) }

  object AttemptFunctorIsomorphism extends Isomorphism.IsoFunctorTemplate[Attempt, Result] {
    def to[A](fa: Attempt[A]) = fa.run
    def from[A](ga: Result[A]) = Attempt(ga)
  }

  implicit object AttemptMonad extends scalaz.IsomorphismMonad[Attempt, Result] {
    def G = Monad[Result]
    def iso = AttemptFunctorIsomorphism
  }

  def AttemptSetIsomorphism[A] = new Isomorphism.IsoSet[Attempt[A], Result[A]] {
    def to = AttemptFunctorIsomorphism.to[A]
    def from = AttemptFunctorIsomorphism.from[A]
  }

  implicit def AttemptMonoid[A: Monoid]: Monoid[Attempt[A]] = new scalaz.IsomorphismMonoid[Attempt[A], Result[A]] {
    def G = Monoid[Result[A]]
    def iso = AttemptSetIsomorphism
  }

  implicit def AttemptEqual[A: Equal] : Equal[Attempt[A]] = new scalaz.IsomorphismEqual[Attempt[A], Result[A]] {
    def G = Equal[Result[A]]
    def iso = AttemptSetIsomorphism
  }
}
