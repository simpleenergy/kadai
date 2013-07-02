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

import scalaz.{ Id, Kleisli, syntax }
import Id._
import syntax.applicative._
import syntax.comonad._
import Configuration.Accessor

/** Sugar for creating ConfigReaders */
trait ConfigReaderInstances {

  /** implicitly summon a ConfigReader using syntax: `ConfigReader[A]` */
  def apply[A: ConfigReader]: ConfigReader[A] =
    implicitly[ConfigReader[A]]

  /** run a ConfigReader by passing in a Configuration */
  def run[A: ConfigReader](config: Configuration): A =
    ConfigReader[A].run(config).go { // run the Free to get the Id[A] out
      _.copoint // Id is a trivial comonad, copoint to get the value
    }

  //
  // factory methods for helping to build ConfigReader instances
  //
  
  /** Build a ConfigReader */
  def apply[A](f: Configuration => A): ConfigReader[A] =
    Kleisli {
      f andThen { _.point[FreeId] }
    }

  /** if you have an Accessor, use it to build a ConfigReader at the specified name */
  def read[A: Accessor](s: String): ConfigReader[A] =
    apply { _[A](s) }

  /** synonym for read */
  def named[A: Accessor](s: String): ConfigReader[A] =
    read(s)

  def option[A: Accessor](s: String): ConfigReader[Option[A]] =
    apply { _.option[A](s) }

  /** pass in the sub-context name */
  def subsection(section: String): ConfigReader[Configuration] =
    apply { extract(section) }

  /** pass in the sub-context name */
  def sub[A](section: String)(f: Configuration => A): ConfigReader[A] =
    apply { extract(section) andThen f }

  /** a constant value */
  def value[A](a: => A): ConfigReader[A] =
    apply { _ => a }
  
  /** produce a reader that runs with a modified config. */
  def local[A](f: Configuration => Configuration)(implicit reader: ConfigReader[A]): ConfigReader[A] =
    apply { c => run(f(c)) }

   /** provides access to the current config */
  def ask: ConfigReader[Configuration] =
    apply { config => config } // cannot use identity for some reason

  private[ConfigReaderInstances] def extract(section: String): Configuration => Configuration =
    _.get[Configuration](section)

  /** syntactic sugar for when we have an explicit ConfigReader we want to execute */
  implicit class ConfigReaderSyntax[A](reader: ConfigReader[A]) {
    def extract(c: Configuration): A =
      run(c)(reader) // explicitly pass the implicit reader param
  }
}
