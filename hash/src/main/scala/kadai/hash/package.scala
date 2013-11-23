package kadai

import java.nio.charset.Charset

package object hash extends HashTypes with StringUtils {
  val charset = Charset.forName("UTF8")
  implicit val codec: io.Codec = charset
}
