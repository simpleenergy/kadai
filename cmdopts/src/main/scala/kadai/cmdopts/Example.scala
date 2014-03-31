/* 
 * Copyright 2012 Atlassian PTY LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kadai
package cmdopts

import nat._

object Example extends App {
  case class OneThing(s: String)
  case class TwoThings(s: String, t: String)

  object CFG extends CmdOpts(List("--all", "--name", "bob", "baz", "--one", "jobbie", "--two", "a", "b")) {
    import CmdOpts._
    // NB: lazy val effectively memoizes the result
    lazy val name = opt("--name", (x: String, y:String) => "%s and %s".format(x, y))
    lazy val onearg = opt("--one", (x: String) => OneThing(x))
    lazy val twoarg = opt("--two", (x: String, y: String) => TwoThings(x,y))
    lazy val all = opt("--all", TRUE)
    lazy val absent = opt("--absent", TRUE)

    override def validate = 
      check(name,"Name not present") &&
      check(absent,"Absent option is required") &&
      check(all,"All option is required")
//    override def validate = check(absent,"Name not present")

    override def version = opt("--version", () => "10.1.5")
    override def usage = opt("--help", () => "Some random help text here")
    // Null implementation will stop the default exit behaviour
    override def handleInfo() { }
  }

  println(CFG.name)
  println("ALL: " + CFG.all)
  println(CFG.absent)
  println(CFG.onearg)
  println(CFG.twoarg)
  println(CFG.validate)
}
