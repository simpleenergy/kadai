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
package concurrent

object Backoff {
  def apply(range: Int) = new Backoff(range)

  import kadai.config.ConfigReader

  def config(section: String, name: String = "backoff"): ConfigReader[Backoff] =
    ConfigReader.sub(section) {
      config => new Backoff(config[Int](name))
    }
}

/** Simple backoff implementation, not exponential, grows randomly but linearly
  */
class Backoff(range: Int) extends (Int => Unit) {
  private val rnd = util.Random

  @throws(classOf[InterruptedException])
  def apply(i: Int): Unit = {
    Thread.sleep {
      rnd.nextInt(range * i).toLong
    }
  }
}