package kadai.concurrent

import java.util.concurrent.{ TimeUnit, CountDownLatch }
import util.control.NonFatal

object Gate {
  def await(latch: CountDownLatch) {
    if (!latch.await(1, TimeUnit.MINUTES))
      throw new TimedOutException
  }

  class TimedOutException extends IllegalStateException
}

class Gate(threads: Int) {
  private[this] val _ready = new CountDownLatch(threads);
  private[this] val _start = new CountDownLatch(1);

  /** wait until the right number of things come in then let them go
    */
  def apply[A, B](a: => A)(f: A => B): B = {
    val param =
      try a
      catch {
        case NonFatal(e) =>
          ready()
          throw e
      }

    ready()
    f(param)
  }

  import Gate._

  /** Called from the racing threads when ready. They will then block until all threads are at this point. */
  def ready() {
    _ready.countDown
    await(_start)
  }

  /** Called from the starter thread. Blocks until everybody is ready, and then signals go. */
  def go() {
    await(_ready)
    _start.countDown
  }
} 
