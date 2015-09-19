import sbt.Keys._

import scala.language.postfixOps

enablePlugins(JavaAppPackaging, BuildInfoPlugin)

name := "Assignment"

normalizedName := "assignment"

description := "Automated system for command suite execution based on Akka actor system and plugins"

licenses in Global +=("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

homepage := Some(url("https://github.com/e8kor/Assignment"))

publishMavenStyle := false

organization := "e8kor"

scalaVersion := "2.11.6"

buildInfoKeys := Seq(name, version, scalaVersion, sbtVersion)

buildInfoPackage := s"${organization value}.${name value}.info"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0" withSources(),
  "com.typesafe" % "config" % "1.2.0" withSources(),
  "org.scalatest" %% "scalatest" % "2.2.5" % "test" withSources(),
  "org.scalaj" %% "scalaj-http" % "1.1.5" withSources(),
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2" withSources(),
  "ch.qos.logback" % "logback-classic" % "1.1.3" withSources()
)

maintainer in Universal := "IEvgenii Korniichuk <nutscracker.ua@gmail.com>"

wixProductId := "ce07be71-510d-414a-92d4-dff47631848a"

wixProductUpgradeId := "4552fb0e-e257-4dbd-9ecb-dba9dbacf424"

scriptClasspath ++= Seq("../conf", "../scripts")

mainClass in Compile := Some("org.assignment.Main")


