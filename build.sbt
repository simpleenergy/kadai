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

organization := "io.kadai"

name := "kadai"

version in ThisBuild := "0.0.1"

licenses := Seq("Apache2" -> url("https://bitbucket.org/atlassian/kadai/raw/master/LICENSE"))

homepage := Some(url("https://bitbucket.org/atlassian/kadai"))

pomExtra := (
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

scalaVersion in ThisBuild := "2.10.0-RC5"

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "releases"  at "http://oss.sonatype.org/content/repositories/releases")

libraryDependencies in ThisBuild ++= Seq(
   "com.chuusai" %  "shapeless"              % "1.2.3"             cross CrossVersion.full
  ,"org.scalaz"  %  "scalaz-core_2.10.0-RC5" % "7.0.0-M6"
  ,"org.specs2"  %  "specs2_2.10.0-RC5"      % "1.13-SNAPSHOT"   % "test"
)

scalacOptions in ThisBuild ++= Seq("-deprecation", "-unchecked", "-feature")

mappings in (Compile, packageBin) ++= Seq( file("LICENSE") -> "META-INF/LICENSE" )
