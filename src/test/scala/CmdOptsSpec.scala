// Copyright 2012 Atlassian PTY LTD
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package io.kadai

import org.specs2.mutable.Specification

class CmdOptsSpec extends Specification {
  case class Person(given: String, family: String)
  case class OneThing(s: String)

  class Conf1(x: Seq[String]) extends CmdOpts(x) {
    lazy val name = opt("--name", (x: String, y: String) => "%s and %s".format(x, y) )
    lazy val person = opt("--name", (x: String, y: String) => Person(x, y) )
    lazy val onething = opt("--one", (x: String) => OneThing(x) )
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

    "One" in {
      val c = new Conf1( List( "--one", "thing" ) )
      c.onething must be equalTo Some(OneThing("thing"))
    }

    // Test unsatisfable view, i.e. generator functionc cannot operate
    "name" in {
      val c = new Conf1( List( "--name", "bar" ) )
      c.name must be equalTo None
      c.person must be equalTo None
    }
  }

}
