package kadai

import java.io._
import scalaz._
import Scalaz._

object Throwables {
  def asString(t: Throwable) = {
    new StringWriter <| {
      sw => t.printStackTrace(new PrintWriter(sw))
    }
  }.toString

  implicit val ShowThrowable = new scalaz.Show[Throwable] {
    override def shows(t: Throwable) = asString(t)
  }
}