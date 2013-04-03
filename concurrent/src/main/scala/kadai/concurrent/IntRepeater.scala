package kadai
package concurrent

import log.Logging

class IntRepeater(i: Int) extends Logging {
  import Logging._
  
  require(i > 0, "Repeat amount must be > 0")

  def times[A](x: => A) = {
    @annotation.tailrec
    def loop(last: A)(left: Int): A =
      if (left <= 0) last
      else loop(x)(left - 1)
    loop(x)(i - 1)
  }

  /**
   * Try executing a function n times.
   * This ignores exceptions until the last try.
   */
  def retries[A](f: => A): A = 
    retriesWith()(f)

  /**
   * Try executing a function n times, executing a backoff strategy in between.
   * This ignores exceptions until the last try.
   */
  def retriesWith[A](backoff: => Any)(f: => A): A = {
    // compiler doesn't optimise tail recursion in catch clauses, hence the workaround using Option
    @annotation.tailrec
    def loop(n: Int): A = {
      try Some(f)
      catch { case e: Exception if n > 1 =>
        withLog("retry-operation failed: [%s] attempt %d of max %d".format(e.getMessage, 1+i-n, i)) { None }
     }
    } match {
      case Some(a) => a
      case None    => backoff; loop(n - 1)
    }
    loop(i)
  }
}
