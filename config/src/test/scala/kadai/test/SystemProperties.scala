package kadai
package test

import org.specs2.mutable.Around
import org.specs2.execute.AsResult

/** Temporarily set a System property for a test. NOT THREADSAFE! obviously */
class SysProp(s: String, v: String) extends Around {
  def around[A: AsResult](a: => A) = {
    val old = Option(System getProperty s)
    System.setProperty(s, v)
    com.typesafe.config.impl.TypesafeConfigWhacker.flushSystemPropertiesCache
    try implicitly[AsResult[A]].asResult(a)
    finally {
      old.map {
        System.setProperty(s, _)
      } getOrElse {
        System clearProperty s
      }
      ()
    }
  }
}