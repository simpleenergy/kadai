package io.kadai
package config

import com.typesafe.config.{ Config, ConfigFactory, ConfigObject, ConfigValue, ConfigResolveOptions, ConfigParseOptions }
import java.io.File
import scalaz.syntax.id._
import org.joda.time.DateTime

/** Simple user-friendly wrapper around Config.
  *
  * Provides a single type-safe apply method for getting values out from the configuration.
  *
  * Usage:
  * {{{
  * val config = Configuration.load("filename.conf").get[Configuration]("objectName")
  * val intThing = config[Int]("intPropertyName")
  * val strThing = config[String]("stringPropertyName")
  * }}}
  * Note that formatting or other problems will throw exceptions.
  *
  * You can also optionally find correct config items or validate and check their correctness (with Either):
  * {{{
  * val intOption:Option[Int] = config.option[Int]("intPropertyName")
  * val strThing: Either[Throwable, String] = config.valid[String]("stringPropertyName")
  * }}}
  *
  * The Accessor type-classes implement the glue to get the specific type configuration item.
  *
  * Details on the underlying configuration file specification can be found here:
  * https://github.com/typesafehub/config/blob/master/HOCON.md
  */
object Configuration {
  import scala.collection.JavaConverters._

  val failIfMissing =
    ConfigParseOptions.defaults.setAllowMissing(false)

  def apply(c: Config) =
    new Configuration(c)

  def from(s: String) =
    Configuration(ConfigFactory parseString s)

  def from(f: File) =
    Configuration {
      ConfigFactory.defaultOverrides.withFallback(ConfigFactory.parseFile(f, failIfMissing))
    }

  /** The path is always relative and on the classpath. */
  def load(path: String) =
    Configuration {
      ConfigFactory.defaultOverrides.withFallback {
        ConfigFactory.load(path, failIfMissing, ConfigResolveOptions.defaults)
      }
    }

  /** The type-class that is used to extract a config item of a particular type. */
  trait Accessor[A] extends ((Config, String) => A)

  //
  // standard type-class instances
  //

  implicit object IntAccessor extends Accessor[Int] {
    def apply(c: Config, s: String) = c getInt s
  }
  implicit object StringAccessor extends Accessor[String] {
    def apply(c: Config, s: String) = c getString s
  }
  implicit object SeqStringAccessor extends Accessor[Seq[String]] {
    def apply(c: Config, s: String) = c getString s split ","
  }
  implicit object ListStringAccessor extends Accessor[List[String]] {
    def apply(c: Config, s: String) = c.getStringList(s).asScala.toList
  }
  implicit object LongAccessor extends Accessor[Long] {
    def apply(c: Config, s: String) = c getLong s
  }
  implicit object BooleanAccessor extends Accessor[Boolean] {
    def apply(c: Config, s: String) = c getBoolean s
  }
  implicit object DateTimeAccessor extends Accessor[DateTime] {
    def apply(c: Config, s: String) = new DateTime(c getMilliseconds s)
  }
  implicit object FileAccessor extends Accessor[File] {
    def apply(c: Config, s: String) = new File(c getString s)
  }
  implicit object ConfigAccessor extends Accessor[Config] {
    def apply(c: Config, s: String) = c getConfig s
  }
  implicit object ConfigurationAccessor extends Accessor[Configuration] {
    def apply(c: Config, s: String) = Configuration(c getConfig s)
  }
  implicit def ClassAccessor[T: Manifest] = new Accessor[Class[T]] {
    def apply(c: Config, s: String): Class[T] =
      Class.forName(c getString s).asInstanceOf[Class[T]] ~~ { cls =>
        manifest[T].runtimeClass |> { expect =>
          if (!(expect isAssignableFrom cls))
            throw new ClassCastException("%s must be a subclass of %s (found [%s])".format(s, expect, cls))
        }
      }
  }
  implicit def ConfigReaderAccessor[A: ConfigReader]: Accessor[A] = new Accessor[A] {
    def apply(c: Config, s: String) =
      Configuration(c).apply[Configuration](s) |> { config =>
        implicitly[ConfigReader[A]].apply(config)
      }
  }

  private[kadai] def asString(c: Configuration): String = c.toConfig.root.render

  // utils

  private[Configuration] val catcher = util.control.Exception.allCatch

  private[kadai] class SerializationProxy(s: String) extends Serializable {
    def readResolve: Object = Configuration from s
  }
}

class Configuration private[Configuration] (val c: Config) extends log.Logging with java.io.Serializable {
  import Configuration._

  def apply[A: Accessor](s: String): A =
    implicitly[Accessor[A]].apply(c, s)

  def get[A: Accessor](s: String): A =
    implicitly[Accessor[A]].apply(c, s)

  def option[A: Accessor](s: String): Option[A] =
    catcher.opt {
      implicitly[Accessor[A]].apply(c, s)
    }

  def valid[A: Accessor](s: String): Either[Throwable, A] =
    catcher.either {
      implicitly[Accessor[A]].apply(c, s)
    }

  def config(s: String): Config =
    implicitly[Accessor[Config]].apply(c, s)

  def toConfig: Config = c

  def overriding(as: (String, String)*) =
    Configuration {
      import collection.JavaConverters._
      ConfigFactory.parseMap(as.toMap.asJava).withFallback(c)
    }

  def withFallback(other: Configuration) = Configuration(c withFallback other.c)

  override def toString = c.root.toString
  override def equals(a: Any) =
    if (!a.isInstanceOf[Configuration]) false
    else c == a.asInstanceOf[Configuration].toConfig
  override def hashCode = c.hashCode

  private def access[A](s: String)(implicit accessor: Accessor[A]): A =
    try accessor.apply(c, s)
    catch {
      case util.control.NonFatal(e) =>
        import scalaz._, Scalaz._
        error(c.toString + "")
        throw e
    }

  private[kadai] def writeReplace: Object = new SerializationProxy(asString(this))
}
