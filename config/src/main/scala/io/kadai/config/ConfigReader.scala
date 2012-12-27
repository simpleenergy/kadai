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
package io.kadai.config

/** Sugar for creating ConfigReaders */
object ConfigReader {
  /** pass in the sub-context name */
  def apply[A](s: String)(f: Configuration => A): ConfigReader[A] =
    new ConfigReader(config => f(config[Configuration](s)))

  /** Lift something into ConfigReader */
  def apply[A](a: => A): ConfigReader[A] =
    new ConfigReader(_ => a)

  /** if you have an Accessor, use it to build a ConfigReader at the specified name */
  def apply[A](accessor: Configuration.Accessor[A])(s: String) =
    new ConfigReader(config => accessor(config.toConfig, s))

  implicit val PointedConfigReader = new scalaz.Pointed[ConfigReader] {
    def point[A](a: => A) = ConfigReader(a)
    def map[A, B](c: ConfigReader[A])(f: A => B) = c map f
  }
}

case class ConfigReader[A](private val f: Configuration => A) extends (Configuration => A) {
  def apply(c: Configuration): A = f(c)

  def map[B](f: A => B): ConfigReader[B] =
    new ConfigReader(this andThen f)

  def flatMap[B](f: A => ConfigReader[B]): ConfigReader[B] =
    new ConfigReader(c => f(this(c))(c))
}

/** Generally used for companion objects that can provide configured implementations */
trait Configurable[A] {
  def config: ConfigReader[A]
}