// Copyright 2012 Atlassian PTY LTD
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import sbt._, Keys._
import sbtrelease.ReleasePlugin._

object KadaiBuild extends Build {
  lazy val projectVersion = "1.1.0-SNAPSHOT"

  lazy val mavenLocal = Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

  lazy val standardSettings = 
    Defaults.defaultSettings ++ 
    releaseSettings ++ // sbt-release
    net.virtualvoid.sbt.graph.Plugin.graphSettings ++ // dependency plugin settings 
    Seq[Project.Setting[_]] (
      organization := "io.kadai"
    , version := projectVersion
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
                  <id>atlassian-public</id>
                  <name>Atlassian Public Repository</name>
                  <url>https://maven.atlassian.com/public</url>
              </repository>
              <snapshotRepository>
                  <id>atlassian-public-snapshot</id>
                  <name>Atlassian Public Snapshot Repository</name>
                  <url>https://maven.atlassian.com/public-snapshot</url>
              </snapshotRepository>
          </distributionManagement>
      )
    , pomIncludeRepository := { (repo: MavenRepository) => false } // no repositories in the pom
    , scalaVersion := "2.10.3"
    , scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-language:_", "-Ywarn-value-discard",  "-Xfatal-warnings", "-Xlog-free-terms")
    , javacOptions ++= Seq("-encoding", "UTF-8")
    , resolvers ++= Seq(
        "Tools Snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots"
      , "Tools Releases"   at "http://oss.sonatype.org/content/repositories/releases"
      , "atlassian-public" at "https://maven.atlassian.com/content/groups/atlassian-public/"
      , "atlassian-internal" at "https://maven.atlassian.com/content/groups/internal/"
      )
    , mappings in (Compile, packageBin) ++= Seq(
        file("LICENSE") -> "META-INF/LICENSE"
      , file("NOTICE")  -> "META-INF/NOTICE"
      )
    , credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
    , addCompilerPlugin("org.scala-lang.plugins" % "macro-paradise" % "2.0.0-SNAPSHOT" cross CrossVersion.full)
    , publishTo in ThisBuild := 
        Some("releases" at "https://maven.atlassian.com/content/groups/public/")
    )

  lazy val core = 
    Project(id = "core"
    , base = file("core")
    , settings = standardSettings
    )

  lazy val logging =
    Project(id = "logging"
    , base = file("logging")
    , settings = standardSettings
    ).dependsOn(core)

  lazy val cmdopts =
    Project(id = "cmdopts"
    , base = file("cmdopts")
    , settings = standardSettings
    ).dependsOn(core)

  lazy val config = 
    Project(id = "config"
    , base = file("config")
    , settings = standardSettings
    ).dependsOn(core, logging)

  lazy val concurrent = 
    Project(id = "concurrent"
    , base = file("concurrent")
    , settings = standardSettings
    ).dependsOn(config)

  lazy val hash = Project(id = "hash"
  , base = file("hash")
  , settings = standardSettings
  ).dependsOn(core)

  lazy val all = 
    Project(id = "all"
    , base = file(".")
    , settings = standardSettings
    ) aggregate (
      core, config, logging, cmdopts, concurrent, hash
    ) dependsOn (
      core, config, logging, cmdopts, concurrent, hash
    )
}
