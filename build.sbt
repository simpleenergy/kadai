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

import AssemblyKeys._

assemblySettings

organization in ThisBuild := "io.kadai"

name := "kadai"

version in ThisBuild := "0.0.4-SNAPSHOT"

scalaVersion in ThisBuild := "2.10.0"

licenses in ThisBuild := Seq("Apache2" -> url("https://bitbucket.org/atlassian/kadai/raw/master/LICENSE"))

homepage in ThisBuild := Some(url("https://bitbucket.org/atlassian/kadai"))

pomExtra in ThisBuild := (
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

resolvers in ThisBuild ++= Seq(
  "Tools Snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots"
, "Tools Releases"   at "http://oss.sonatype.org/content/repositories/releases"
, "atlassian-public" at "https://m2proxy.atlassian.com/content/groups/atlassian-public/"
, "atlassian-internal" at "https://m2proxy.atlassian.com/content/groups/internal/"
// Contegix, m2proxy.atlassian.com is borked, m2proxy-int.private.atlassian.com works
, "atlassian-public-ctx" at "http://m2proxy-int.private.atlassian.com/content/groups/atlassian-public/"
, "atlassian-internal-ctx" at "http://m2proxy-int.private.atlassian.com/content/groups/internal/"
)

libraryDependencies in ThisBuild ++= Seq(
  "org.specs2"   %%  "specs2"    % "1.13"       % "test"
)

scalacOptions in ThisBuild ++= Seq("-deprecation", "-unchecked", "-feature", "-language:_")

mappings in (Compile, packageBin) ++= Seq(
   file("LICENSE") -> "META-INF/LICENSE"
  ,file("NOTICE")  -> "META-INF/NOTICE"
)

EclipseKeys.withSource in ThisBuild := true

EclipseKeys.createSrc in ThisBuild := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

assembleArtifact in packageScala := false
