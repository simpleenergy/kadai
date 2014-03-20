package kadai.config

import scalaz.{ Free, ReaderT }
import scalaz.Scalaz.Id

trait ConfigReaderTypes {
  // trampoline ConfigReaders so we don't use up stack
  type FreeId[+A] = Free[Id, A]
  type ConfigReader[+A] = ReaderT[FreeId, Configuration, A]

  object ConfigReader extends ConfigReaderInstances
}