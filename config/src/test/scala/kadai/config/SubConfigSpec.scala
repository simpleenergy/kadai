package kadai.config

class SubConfigSpec extends org.specs2.mutable.Specification {

  val root = Configuration.load("test.conf")
  val test = root.get[Configuration]("thing")

  "Configuration URL" should {
    "be substituted in subconfig" in {
      test[String]("someUrl") must be equalTo "http://localhost"
    }
    "be substituted in main" in {
      root[String]("thing.someUrl") must be equalTo "http://localhost"
    }
  }
}