import sbt._
import Keys._

object AtlassianPrivate extends Plugin {
  override def settings = 
    Seq(
      publishTo <<= version { (v: String) =>
        val nexus = "https://maven.atlassian.com/"

        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "private-snapshot")
        else
          Some("releases"  at nexus + "private")
      }
    )
}
