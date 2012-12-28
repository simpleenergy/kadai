import sbt._, Keys._

object KadaiBuild extends Build {
  lazy val standardSettings = Defaults.defaultSettings ++ List[Project.Setting[_]] (
    organization := "io.kadai"
  , version := "0.0.4-SNAPSHOT"
  , licenses := Seq("Apache2" -> url("https://bitbucket.org/atlassian/kadai/raw/master/LICENSE"))
  , homepage := Some(url("https://bitbucket.org/atlassian/kadai"))
  , pomExtra := (
        <scm>
            <url>git@bitbucket.org:atlassian/kadai.git</url>
            <connection>scm:git:git@bitbucket.org:atlassian/kadai.git</connection>
            <developerConnection>scm:git:git@bitbucket.org:atlassian/kadai.git</developerConnection>
        </scm>
        <distributionManagement>
            <repository>
                <id>atlassian-private</id>
                <name>Atlassian Private Repository</name>
                <url>https://maven.atlassian.com/private</url>
            </repository>
            <snapshotRepository>
                <id>atlassian-private-snapshot</id>
                <name>Atlassian Private Snapshot Repository</name>
                <url>https://maven.atlassian.com/private-snapshot</url>
            </snapshotRepository>
        </distributionManagement>
    )
  , scalaVersion := "2.10.0"
  , scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-language:_")
  , resolvers ++= Seq(
      "Tools Snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots"
    , "Tools Releases"   at "http://oss.sonatype.org/content/repositories/releases"
    , "atlassian-public" at "https://m2proxy.atlassian.com/content/groups/atlassian-public/"
    , "atlassian-internal" at "https://m2proxy.atlassian.com/content/groups/internal/"
      // Contegix, m2proxy.atlassian.com is borked, m2proxy-int.private.atlassian.com works
    , "atlassian-public-ctx" at "http://m2proxy-int.private.atlassian.com/content/groups/atlassian-public/"
    , "atlassian-internal-ctx" at "http://m2proxy-int.private.atlassian.com/content/groups/internal/"
    )
  , mappings in (Compile, packageBin) ++= Seq(
      file("LICENSE") -> "META-INF/LICENSE"
    , file("NOTICE")  -> "META-INF/NOTICE"
    )
  )

  lazy val core = Project(id = "core"
  , base = file("core")
  , settings = standardSettings
  )

  lazy val logging = Project(id = "logging"
  , base = file("logging")
  , settings = standardSettings
  ).dependsOn(core)

  lazy val cmdopts = Project(id = "cmdopts"
  , base = file("cmdopts")
  , settings = standardSettings
  ).dependsOn(core)

  lazy val config = Project(id = "config"
  , base = file("config")
  , settings = standardSettings
  ).dependsOn(core, logging)

  lazy val all = Project(id = "all"
  , base = file(".")
  , settings = standardSettings
  ) aggregate (core, config, logging, cmdopts) dependsOn (core, config, logging, cmdopts)
}
