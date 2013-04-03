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
package kadai.concurrent

class IntRepeaterSpec extends org.specs2.mutable.SpecificationWithJUnit {

  import IntRepeater._

  // remove when https://issues.apache.org/jira/browse/LOG4J2-169 is fixed
  sequential
  
  "No fails" should {
    val u = new Unstable(1, 0)
    "only run once" in { 1 retries { u.run must be equalTo 1 } }
  }

  "Recoverable fails" should {
    val u = new Unstable(3, 3)
    "run successfully despite exceptions" in { 3 retries { u.run must be equalTo 3 } }
  }

  "Too many fails" should {
    val u = new Unstable(4, 4)
    "throw an exception" in { 3 retries { u.run must throwA[UnstableException](message = "run #3") } }
  }

  "Recoverable fails" should {
    "run successfully despite exceptions" in {
      val u = new Unstable(3, 3)
      var retry = 0
      3.retriesWith(retry += 1) {
        retry must be equalTo 2
      }
    }
  }
}

case class Unstable(runs: Int, throws: Int) {
  private var c = 0

  def run = {
    c += 1
    if (c < throws) throw new UnstableException(c)
    c
  }
}

class UnstableException(run: Int) extends RuntimeException("run #" + run)

