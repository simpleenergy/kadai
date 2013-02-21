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
package kadai.config

import scalaz.Reader

/** ConfigReader is the Reader monad specialized to the Configuration class */

/** Sugar for creating ConfigReaders */
trait ConfigReaderInstances {
  /** Lift something into ConfigReader */
  def apply[A](f: Configuration => A): ConfigReader[A] =
    Reader(f)

  /** if you have an Accessor, use it to build a ConfigReader at the specified name */
  def named[A: Configuration.Accessor](s: String): ConfigReader[A] =
    apply {
      c => implicitly[Configuration.Accessor[A]].apply(c.toConfig, s)
    }

  /** pass in the sub-context name */
  def sub[A](section: String)(f: Configuration => A): ConfigReader[A] =
    apply(extract(section) andThen f)

  private[ConfigReaderInstances] def extract(section: String): Configuration => Configuration =
    _.get[Configuration](section)

  implicit val MonadConfigReader = new scalaz.Monad[ConfigReader] {
    def point[A](a: => A) = Reader(_ => a)
    def bind[A, B](c: ConfigReader[A])(f: A => ConfigReader[B]) = c flatMap f
  }
}

/** Generally used for companion objects that can provide configured implementations */
trait Configurable[A] {
  def config: ConfigReader[A]
}