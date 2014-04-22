package kadai

import scalaz.{ Equal, Isomorphism, IsomorphismEqual, IsomorphismMonad, IsomorphismMonoid, Monad, Monoid, ~>, \/ }
import scalaz.effect.IO
import scalaz.syntax.either._
import scalaz.syntax.std.option._

/**
 * Represents the result of an action, which may be the result itself or an Invalid (error).
 *
 * An unsafe operation may be attempted using `Attempt.safe(unsafeCall)` for instance:
 *
 * {{{
 * val r: Attempt[String] = for {
 *   a <- Attempt.safe { throw new RuntimeException("oh noes!") }
 *   b <- Attempt.ok("should be ok!")
 * } yield b
 * }}}
 *
 * will result in an Attempt that holds an Invalid.Err with the RuntimeException in it.
 *
 * This class does not – and will not – auto-magically catch exceptions for you in `map`/`flatMap`.
 * 
 * @since 1.2
 */
case class Attempt[+A](run: Invalid \/ A) {
  def map[B](f: A => B): Attempt[B] =
    Attempt(run map f)

  def flatMap[B](f: A => Attempt[B]): Attempt[B] =
    Attempt(run flatMap { f(_).run })

  def toOr: Invalid \/ A =
    run

  def toOption: Option[A] =
    run.toOption

  def lift[B](fn: Invalid \/ A => Invalid \/ B): Attempt[B] =
    Attempt(fn(run))

  /** 
   * Catamorphism. Run the first given function if left, otherwise, the second given function.
   * @since 1.3
   */
  def fold[B](l: => Invalid => B, r: A => B): B =
    run.fold(l, r)
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
    catch { case util.control.NonFatal(t) => exception(t) }

  val i = 1.some
  
  def safely[A](op: IO[A]): IO[Attempt[A]] =
    op.catchSomeLeft { _.invalid.some }.map { Attempt.apply }

  //
  // typeclasses
  //

  /**
   * Attempt <~> Result is the IsoFunctor, or NaturalTransformation
   * Isomophism, between Attempt and its underlying Result.
   *
   * We use this to remove all the boilerplate from the typeclass
   * instance declarations, and just use Result's definitions.
   */
  object AttemptIso extends Isomorphism.IsoFunctorTemplate[Attempt, Result] {
    def to[A](fa: Attempt[A]) = fa.run
    def from[A](ga: Invalid \/ A) = Attempt(ga)
  }

  object ToOr extends (Attempt ~> Result) {
    def apply[A](fa: Attempt[A]): Invalid \/ A = fa.toOr
  }

  object ToOption extends (Attempt ~> Option) {
    def apply[A](fa: Attempt[A]): Option[A] = fa.toOption
  }

  implicit object AttemptMonad extends IsomorphismMonad[Attempt, Result] {
    val G = Monad[Result]
    def iso = AttemptIso
  }

  implicit def AttemptMonoid[A: Monoid]: Monoid[Attempt[A]] = new IsomorphismMonoid[Attempt[A], Invalid \/ A] {
    val G = Monoid[Invalid \/ A]
    val iso: Isomorphism.IsoSet[Attempt[A], Invalid \/ A] = AttemptSetIsomorphism
  }

  implicit def AttemptEqual[A: Equal]: Equal[Attempt[A]] = new IsomorphismEqual[Attempt[A], Invalid \/ A] {
    val G = Equal[Invalid \/ A]
    val iso: Isomorphism.IsoSet[Attempt[A], Invalid \/ A] = AttemptSetIsomorphism
  }

  private def AttemptSetIsomorphism[A] = new Isomorphism.IsoSet[Attempt[A], Invalid \/ A] {
    def to = AttemptIso.to[A]
    def from = AttemptIso.from[A]
  }
}
