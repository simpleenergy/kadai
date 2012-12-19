package io.kadai

import org.specs2.mutable.Specification

class CmdOptsSpec extends Specification {
  case class Person(given: String, family: String)

  class Conf1(x: Seq[String]) extends CmdOpts(x) {
    lazy val name = opt("--name", (x: (String, String)) => "%s and %s".format(x._1, x._2) )
    lazy val person = opt("--name", (x: (String, String)) => Person(x._1, x._2) )
    lazy val all = opt("--all", TRUE)
    lazy val absent = opt("--absent", TRUE)
    override def version = opt("--version",() => "10.1.5")
    override def usage = opt("--help",() => "Some random help text here")
    override def handle_info() { } // Ensures that we do not exit, which is the default
  }

  "Simple Tests" should {
    // Test two views onto --name <arg1> <arg2>
    "name" in {
      val c = new Conf1( List( "--name", "bar", "baz" ) )
      c.name must be equalTo Some("bar and baz")
      c.person must be equalTo Some(Person("bar","baz"))
    }

    // Test view onto --all, just a simple flag
    "flag" in {
      val c = new Conf1( List( "--all" ) )
      c.all must be equalTo Some(true)
    }

    "absent" in {
      val c = new Conf1( List( "--all" ) )
      c.absent must be equalTo None
    }

    // Test views onto an empty list are sound
    "empty" in {
      val c = new Conf1( List[String]() )
      c.name must be equalTo None
      c.person must be equalTo None
      c.all must be equalTo None
      c.absent must be equalTo None
    }

    // Test unsatisfable view, i.e. generator functionc cannot operate
    "name" in {
      val c = new Conf1( List( "--name", "bar" ) )
      c.name must be equalTo None
      c.person must be equalTo None
    }
  }

}
