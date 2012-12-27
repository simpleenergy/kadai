import sbt._, Keys._

object ThingBuild extends Build {

  lazy val core = Project(id = "core", base = file("core")) 
  lazy val config = Project(id = "config", base = file("config")).dependsOn(core)
  lazy val log = Project(id = "log", base = file("log")).dependsOn(core)
  lazy val cmdopts = Project(id = "cmdopts", base = file("cmdopts")).dependsOn(core)

  lazy val all = Project(id = "all", base = file(".")) aggregate (core, config, log, cmdopts)
}
