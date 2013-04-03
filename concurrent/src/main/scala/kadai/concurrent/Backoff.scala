package kadai
package concurrent

object Backoff {
  def apply(range: Int) = new Backoff(range)

  import kadai.config.ConfigReader

  def config(section: String, name: String = "backoff"): ConfigReader[Backoff] =
    ConfigReader.sub(section) {
      config => new Backoff(config[Int](name))
    }
}

/** Simple backoff implementation, not exponential, grows randomly but linearly
  */
class Backoff(range: Int) extends (() => Unit) {
  private val rnd = util.Random
  private var pause: Int = _

  @throws(classOf[InterruptedException])
  def apply() {
    pause += rnd.nextInt(range)
    Thread.sleep(pause)
  }
}