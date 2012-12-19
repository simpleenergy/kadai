import AssemblyKeys._

name := "scmdopts"

version in ThisBuild := "0.0.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.10.0-RC5"

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers in ThisBuild ++= Seq(
  "mvn-local" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
, Classpaths.typesafeResolver
, "atlassian-public" at "https://m2proxy.atlassian.com/content/groups/atlassian-public/"
, "atlassian-internal" at "https://m2proxy.atlassian.com/content/groups/internal/"
// Contegix, m2proxy.atlassian.com is borked, m2proxy-int.private.atlassian.com works
, "atlassian-public-ctx" at "http://m2proxy-int.private.atlassian.com/content/groups/atlassian-public/"
, "atlassian-internal-ctx" at "http://m2proxy-int.private.atlassian.com/content/groups/internal/"
)

libraryDependencies in ThisBuild ++= Seq(
   "com.chuusai" %  "shapeless"              % "1.2.3"           cross CrossVersion.full
   ,"org.scalaz" %  "scalaz-core_2.10.0-RC5" % "7.0.0-M6"
   ,"org.specs2" %  "specs2_2.10.0-RC5"      % "1.13-SNAPSHOT"          % "test"
)

scalacOptions in ThisBuild ++= Seq("-deprecation", "-unchecked", "-feature")

assemblySettings
