import sbt._, Keys._

object ThingBuild extends Build {

  lazy val core = Project(id = "core", base = file("core")) 
  lazy val logging = Project(id = "logging", base = file("logging")).dependsOn(core)
  lazy val cmdopts = Project(id = "cmdopts", base = file("cmdopts")).dependsOn(core)
  lazy val config = Project(id = "config", base = file("config")).dependsOn(core, logging)

  lazy val all = Project(id = "all", base = file(".")) aggregate (core, config, logging, cmdopts) dependsOn (core, config, logging, cmdopts)
}
