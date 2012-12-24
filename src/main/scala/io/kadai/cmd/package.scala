package io.kadai

package object cmd {
  private[cmd] def tailOption[A](as: Seq[A]):Option[Seq[A]] =
    if (as.isEmpty) None
    else {
      val tail = as.tail
      if (tail.isEmpty) None
      else Some(tail)
    }
}