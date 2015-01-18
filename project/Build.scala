import sbt.Keys._
import sbt._
import sbtassembly.Plugin.AssemblyKeys._
import sbtassembly.Plugin._

object ApplicationBuild extends Build {

  lazy val appDependencies = Seq(
  	"io.reactivex" % "rxjava" % "1.0.0" withSources() withJavadoc(),
    "org.glassfish.jersey.core" % "jersey-client" % "2.13" withSources() withJavadoc(),
    "com.ning" % "async-http-client" % "1.8.14" withSources() withJavadoc()
  )

  lazy val testDependencies = Seq (
    "com.novocode" % "junit-interface" % "0.11" % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test" exclude("org.hamcrest", "hamcrest-core"),
    "org.hamcrest" % "hamcrest-all" % "1.3" % "test"
  )

  val appReleaseSettings = Seq(
    // Publishing options:
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { x => false }
  )

  def defaultResolvers = Seq(

  )

  def commonSettings = Seq(
    organization := "pl.test.rx",
    autoScalaLibrary := false,
    scalaVersion := "2.10.2",
    crossPaths := false,
    resolvers ++= defaultResolvers
  )

  def standardSettingsWithAssembly = commonSettings ++ assemblySettings ++ appReleaseSettings ++ Seq(
    mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
      {
        case "about.html" => MergeStrategy.rename
        case "META-INF/spring.tooling" => MergeStrategy.discard
        case x => old(x)
      }
    },
    test in assembly := {}

  )

  lazy val rxJavaTest = Project("rx-java-test", file("."),
    settings = standardSettingsWithAssembly ++ Seq(
      name := "rx-java-test",
      libraryDependencies ++= appDependencies ++ testDependencies
    ))
}
