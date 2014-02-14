package kadai

import util.control.NonFatal
import scalaz.{ Equal, Monad, \/ }
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

  implicit object AttemptMonad extends Monad[Attempt] {
    def point[A](v: => A) = ok(v)

    def bind[A, B](m: Attempt[A])(f: A => Attempt[B]) =
      m flatMap f

    override def map[A, B](m: Attempt[A])(f: A => B) =
      m map f
  }

  implicit def AttemptEqual[A: Equal]: Equal[Attempt[A]] =
    new Equal[Attempt[A]] {
      def equal(a1: Attempt[A], a2: Attempt[A]) =
        Equal[Result[A]].equal(a1.run, a2.run)
    }
}