import AssemblyKeys._

name := "scmdopts"

version in ThisBuild := "0.0.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.10.0-RC5"

resolvers in ThisBuild ++= Seq(
   "mvn-local" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
  ,"Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/"
  ,"Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
  ,Classpaths.typesafeResolver
  ,"atlassian-public" at "https://m2proxy.atlassian.com/content/groups/atlassian-public/"
)

// don't check the consistency, JIRA has some very dodgy dependencies
resolvers in ThisBuild += {
  val r = new org.apache.ivy.plugins.resolver.IBiblioResolver
  r.setM2compatible(true)
  r.setName("Internal")
  r.setRoot("https://m2proxy.atlassian.com/content/groups/internal/")
  r.setCheckconsistency(false)
  new RawRepository(r)
}

libraryDependencies in ThisBuild ++= Seq(
   "com.chuusai" % "shapeless" % "1.2.3" cross CrossVersion.full
)

scalacOptions in ThisBuild ++= Seq("-deprecation", "-unchecked", "-feature")

assemblySettings
