package kadai.config

class ConfigurationSerializationSpec extends org.specs2.mutable.Specification {
  "A Configuration" should {
    "be serializable" in {
      import java.io._
      val byteStream = new ByteArrayOutputStream()
      val out = new ObjectOutputStream(byteStream)
      val conf = Configuration from "config {}"
      out.writeObject(conf)
      byteStream.toByteArray().size must be >= 1
    }
  }
}