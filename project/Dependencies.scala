import sbt._
import Keys._

object Dependencies {

  val scalaVersionString = "2.11.8"

  val akkaStreamDep:     ModuleID = "com.typesafe.akka"          %% "akka-stream"           % "2.4.11"
  val cassandraDep:      ModuleID = "com.datastax.cassandra"     %  "cassandra-driver-core" % "3.1.1"
  val json4sDep:         ModuleID = "org.json4s"                 %% "json4s-native"         % "3.4.1"
  val kxbmapConfigsDep:  ModuleID = "com.github.kxbmap"          %% "configs"               % "0.4.3"
  val mongodbDep:        ModuleID = "org.mongodb"                %  "mongodb-driver"        % "3.3.0"
  val nScalaTimeDep:     ModuleID = "com.github.nscala-time"     %% "nscala-time"           % "2.14.0"
  val scalaLoggingDep:   ModuleID = "com.typesafe.scala-logging" %% "scala-logging"         % "3.5.0"
  val scalaReflectDep:   ModuleID = "org.scala-lang"             %  "scala-reflect"         % scalaVersionString
  val scalaTestDep:      ModuleID = "org.scalatest"              %% "scalatest"             % "2.2.6"
  val slf4jSimpleDep:    ModuleID = "org.slf4j"                  %  "slf4j-simple"          % "1.7.21"
  val typesafeConfigDep: ModuleID = "com.typesafe"               %  "config"                % "1.3.1"

}
