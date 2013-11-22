package kadai

import java.nio.charset.Charset

package object hash extends HashTypes {
  val charset = Charset.forName("UTF8")
  implicit val codec: io.Codec = charset
}
