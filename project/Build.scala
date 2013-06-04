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

object KadaiBuild extends Build {
  lazy val projectVersion = "0.0.5-RC2"

  lazy val mavenLocal = Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

  lazy val standardSettings = Defaults.defaultSettings ++ Seq[Project.Setting[_]] (
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
  , scalaVersion := "2.10.1"
  , scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-language:_")
  , resolvers ++= Seq(
      "Tools Snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots"
    , "Tools Releases"   at "http://oss.sonatype.org/content/repositories/releases"
    , "atlassian-public" at "https://maven.atlassian.com/content/groups/atlassian-public/"
    , "atlassian-internal" at "https://maven.atlassian.com/content/groups/internal/"
      // Contegix, m2proxy.atlassian.com is borked, m2proxy-int.private.atlassian.com works
    //, "atlassian-public-ctx" at "http://m2proxy-int.private.atlassian.com/content/groups/atlassian-public/"
    //, "atlassian-internal-ctx" at "http://m2proxy-int.private.atlassian.com/content/groups/internal/"
    )
  , mappings in (Compile, packageBin) ++= Seq(
      file("LICENSE") -> "META-INF/LICENSE"
    , file("NOTICE")  -> "META-INF/NOTICE"
    )
  , libraryDependencies ++= Seq("org.specs2" %%  "specs2" % "1.13" % "test")
  , credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
  ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings // add dependency plugin settings

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

  lazy val concurrent = Project(id = "concurrent"
  , base = file("concurrent")
  , settings = standardSettings
  ).dependsOn(config)

  lazy val all = Project(id = "all"
  , base = file(".")
  , settings = standardSettings
  ) aggregate (core, config, logging, cmdopts, concurrent) dependsOn (core, config, logging, cmdopts, concurrent)
}
