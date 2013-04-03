package kadai.concurrent

object Pool {
  val empty = Pool[Nothing]()
}

/**
 * Pool manages two queues of available and busy objects. While the queues
 * are immutable, Pool itself is not thread safe unless created through
 * AtomicPool.
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
 * Creates a threadsafe Pool wrapper
 */
object AtomicPool {
  def apply[A](available: List[A] = Nil, busy: List[A] = Nil): Atomic[Pool[A]] = Atomic(Pool[A](available, busy))
}