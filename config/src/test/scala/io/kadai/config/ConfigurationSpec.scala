package io.kadai
package config

import scalaz.syntax.id._

class ConfigurationSpec extends org.specs2.mutable.Specification {

  val root = Configuration.load("test.conf")
  val engine = root.get[Configuration]("engine")

  sequential

  "Engine Configuration" should {
    "not be null" in { engine must not beNull }
    "have a test" in { engine[String]("test") must be equalTo "demo" }
  }

  "Host Configuration" should {
    val host = engine.get[Configuration]("host")
    "not be null" in { host must not beNull }
    "have a url" in { host[String]("url") must be equalTo "http://localhost" }
    "have a port" in { host[Int]("port") must be equalTo 8000 }
    "have a proxyPort" in { host[Int]("proxyBasePort") must be equalTo 9000 }
  }

  "config" should {
    val config = root.config("config")
    "not be null" in { config must not beNull }
    "have a string" in { config.getString("string") must be equalTo "some" }
    "have a number" in { config.getInt("number") must be equalTo 42 }
    "have an embedded config object" in {
      config.getConfig("embed").getConfig("more").getString("string") must be equalTo "woohoo"
    }
  }

  "Configuration objects" should {
    val config = root[Configuration]("config")
    "have a valid string" in { config.valid[String]("string") must be equalTo Right("some") }
    "have a valid number" in { config.valid[Int]("number") must be equalTo Right(42) }
    "not be able to parse a number from a String (as valid)" in {
      config.valid[Int]("string").isLeft must be equalTo true
    }
    "have an optional string" in { config.option[String]("string") must be equalTo Some("some") }
    "have an optional number" in { config.option[Int]("number") must be equalTo Some(42) }
    "not be able to parse a number from a String (as a none)" in {
      config.option[Int]("string") must be equalTo None
    }
  }

  "thing from factory" should {
    implicit val ThingFactory = ConfigReader {
      (config: Configuration) => new Thing(config[Int]("id"))
    }

    val thing = root[Thing]("thing")
    "not be null" in { thing must not beNull }
    "have an ID" in { thing.id must be equalTo 667 }
  }

  "Configuration.from(String)" should {
    val str = Configuration.from("""
        embed {
          string = woohoo
          int = 12
        }""")
    "not be null" in { str must not beNull }
    "have embedded" in { str[Configuration]("embed") must not beNull }
    "have a string" in { str[String]("embed.string") must be equalTo "woohoo" }
    "have an int" in { str[Int]("embed.int") must be equalTo 12 }
  }

  "Configuration.overriding" should {
    val str = Configuration.from("""
        embed {
          string = woohoo
          int = 12
        }""").overriding("embed.string" -> "haha!", "embed.int" -> "42")
    "not be null" in { str must not beNull }
    "have embedded" in { str[Configuration]("embed") must not beNull }
    "have a string" in { str[String]("embed.string") must be equalTo "haha!" }
    "have an int" in { str[Int]("embed.int") must be equalTo 42 }
  }

  val file = {
    val name = "src/test/resources/test.conf"
    val mvn = new java.io.File(name)
    if (mvn.isFile()) mvn
    else new java.io.File("config/" + name) // sbt runs in parent
  }
  "Loading from file" should {
    "not fail if the file is existing" in {
      Configuration.from(file) |> (_[String]("thing.id") must be equalTo "667")
    }

    "fail if the file is not existing" in {
      Configuration.from(new java.io.File("nosuchfile.conf")) must throwA[com.typesafe.config.ConfigException.IO]
    }
  }

  "Loading from classpath" should {
    "not fail if the resource is existing" in {
      Configuration.load("test.conf") |> (_[String]("thing.id") must be equalTo "667")
    }

    "fail if the resource is not existing" in {
      Configuration.load("nosuchfile.conf") must throwA[com.typesafe.config.ConfigException.IO]
    }
  }

  "System properties" should {
    "override when loading from the Classpath" in new test.SysProp("thing.id", "666") {
      Configuration.load("test.conf") |> (_[String]("thing.id") must be equalTo "666")
    }

    "override when loading from a file" in new test.SysProp("thing.id", "666") {
      Configuration.from(file) |> (_[String]("thing.id") must be equalTo "666")
    }
  }
}

class Thing(val id: Int)


