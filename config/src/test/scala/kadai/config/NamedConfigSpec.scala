package kadai.config

import scalaz.syntax.id._
import ConfigReader._
import com.typesafe.config.Config

class NamedConfigSpec extends org.specs2.mutable.Specification {

  val root = Configuration.load("test.conf")
  val test = root.get[Configuration]("thing")
  case class Server(url: String, port: Int)

  implicit val ServerAccessor = new Configuration.Accessor[Server] {
    def apply(c: Config, s: String): Server =
      Configuration(c).apply[Configuration](s) |> {
        config => Server(config[String]("url"), config[Int]("port"))
      }
  }

  "Named ConfigReader" should {
    "access the subconfig" in {
      ConfigReader.named[Server]("engine.host").extract(root) must
        be equalTo Server("http://localhost", 8000)
    }
  }
}