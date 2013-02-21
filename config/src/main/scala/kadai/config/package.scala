package kadai

package object config {
  type ConfigReader[A] = scalaz.Reader[Configuration, A]

  object ConfigReader extends ConfigReaderInstances
}