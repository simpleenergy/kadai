package io.kadai.config

/** Sugar for creating ConfigReaders */
object ConfigReader {
  /** pass in the sub-context name */
  def apply[A](s: String)(f: Configuration => A): ConfigReader[A] =
    new ConfigReader(config => f(config[Configuration](s)))

  /** Lift something into ConfigReader */
  def apply[A](a: => A): ConfigReader[A] =
    new ConfigReader(_ => a)

  /** if you have an Accessor, use it to build a ConfigReader at the specified name */
  def apply[A](accessor: Configuration.Accessor[A])(s: String) =
    new ConfigReader(config => accessor(config.toConfig, s))

  implicit val PointedConfigReader = new scalaz.Pointed[ConfigReader] {
    def point[A](a: => A) = ConfigReader(a)
    def map[A, B](c: ConfigReader[A])(f: A => B) = c map f
  }
}

case class ConfigReader[A](private val f: Configuration => A) extends (Configuration => A) {
  def apply(c: Configuration): A = f(c)

  def map[B](f: A => B): ConfigReader[B] =
    new ConfigReader(this andThen f)

  def flatMap[B](f: A => ConfigReader[B]): ConfigReader[B] =
    new ConfigReader(c => f(this(c))(c))
}

/** Generally used for companion objects that can provide configured implementations */
trait Configurable[A] {
  def config: ConfigReader[A]
}