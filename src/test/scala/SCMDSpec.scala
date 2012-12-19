package com.atlassian.kadai

import org.specs2.mutable.Specification

class SCMDSpec extends Specification {
  class Conf1(x: Seq[String]) extends SCMD(x) {
    lazy val name = opt("--name", (x: (String, String)) => "%s and %s".format(x._1, x._2) )
    lazy val all = opt("--all", TRUE)
    lazy val absent = opt("--absent", TRUE)
    override def version = opt("--version",() => "10.1.5")
    override def usage = opt("--help",() => "Some random help text here")
    override def handle_info() { }
  }

  "Simple Tests" should {
    "name" in {
      val c = new Conf1( List( "--name", "bar", "baz" ) )
      c.name must be equalTo Some("bar and baz")
    }
  }

}
