package com.typesafe.config.impl

/** Typesafe config caches system properties on classloading, but provides
 * a package-private implementation of flushing for unit tests. This bridges
 * over (see package above) to get around the visibility restriction.
 */
object TypesafeConfigWhacker {

  def flushSystemPropertiesCache =
    ConfigImpl.reloadSystemPropertiesConfig
}