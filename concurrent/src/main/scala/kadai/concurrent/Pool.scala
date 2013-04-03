package kadai.concurrent

object Pool {
  val empty = Pool[Nothing]()
}

/**
 * Pool manages two queues of available and busy objects.
 * 
 * Immutable, variable reference needs to be managed externally.
 */
case class Pool[+A](available: List[A] = Nil, busy: List[A] = Nil) {

  def ::[B >: A](a: B) = add(a)

  def -:[B >: A](a: B) = remove(a)

  def add[B >: A](a: B) =
    copy(available = a :: available)

  def remove[B >: A](a: B) =
    copy(available = available.filterNot(_ == a), busy = busy.filterNot(_ == a))

  def borrow: (Pool[A], Option[A]) =
    available.headOption match {
      case None        => this -> None
      case s @ Some(a) => copy(available = available.tail, busy = a :: busy) -> s
    }

  def borrowWhile(f: A => Boolean): (Pool[A], Option[Seq[A]]) =
    available span f match {
      case (Nil, _)     => this -> None
      case (as, remain) => copy(available = remain, busy = as ::: busy) -> Some(as)
    }

  def giveBack[B >: A](a: B): Pool[B] =
    copy(available = a :: available, busy = busy.filterNot(_ == a))
}

/**
 * Creates a thread-safe mutable Pool wrapper
 */
object AtomicPool {
  def apply[A](available: List[A] = Nil, busy: List[A] = Nil): Atomic[Pool[A]] = Atomic(Pool(available, busy))
}