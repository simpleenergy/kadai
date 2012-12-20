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

object Example extends App {
  object CFG extends CmdOpts(List("--name", "bob", "baz")) {
    // NB: lazy val effectively memoizes the result
    lazy val name = opt("--name", (x: (String, String)) => "%s and %s".format(x._1, x._2))
    lazy val all = opt("--all", TRUE)
    lazy val absent = opt("--absent", TRUE)
    override def version = opt("--version", () => "10.1.5")
    override def usage = opt("--help", () => "Some random help text here")
  }
  println(CFG.name)
  println("ALL: " + CFG.all)
  println(CFG.absent)
}
