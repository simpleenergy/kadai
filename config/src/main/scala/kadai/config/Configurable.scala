package kadai.config

/** Generally used for companion objects that can provide configured implementations */
trait Configurable[A] {
  def config: ConfigReader[A]
}
