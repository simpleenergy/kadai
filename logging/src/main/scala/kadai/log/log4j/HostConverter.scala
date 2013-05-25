package kadai.log.log4j

import org.apache.logging.log4j.core
import core.config.plugins.Plugin
import core.pattern.{ ConverterKeys, LogEventPatternConverter }

/**
 * Adds the host name as a log element, use %host in the pattern to access.
 */
object HostConverter {
  def newInstance(options: Array[String]): HostConverter = {
    val numberOfElements: Int = options.headOption.map { _.toInt } getOrElse 1

    new HostConverter(
      java.net.InetAddress.getLocalHost.getHostName.
        split("\\.").
        take(numberOfElements).
        mkString(".")
    )
  }
}

@Plugin(name = "host", category = "Converter")
@ConverterKeys(Array("host"))
class HostConverter(host: String) extends LogEventPatternConverter("hostname", "") {
  def format(e: core.LogEvent, sb: java.lang.StringBuilder) {
    sb.append(host);
  }
}