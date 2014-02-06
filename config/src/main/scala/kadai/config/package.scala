package kadai

import scalaz._
import Id._

package object config {
  // trampoline ConfigReaders so we don't use up stack
  type FreeId[+A] = Free[Id, A]
  type ConfigReader[A] = ReaderT[FreeId, Configuration, A]

  object ConfigReader extends ConfigReaderInstances
}