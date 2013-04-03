package kadai.concurrent

/** Thread-safe monotonic sequence generator */
class SequenceGenerator(start: Int = 0) {
  private val gen = new java.util.concurrent.atomic.AtomicInteger(start)
  def apply() = gen.getAndIncrement
}