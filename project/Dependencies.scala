import sbt._
import Keys._

object Dependencies {

  val scalaVersionString = "2.12.1"

  val akkaStreamDep:     ModuleID = "com.typesafe.akka"          %% "akka-stream"           % "2.4.14"
  val cassandraDep:      ModuleID = "com.datastax.cassandra"     %  "cassandra-driver-core" % "3.1.2"
  val json4sDep:         ModuleID = "org.json4s"                 %% "json4s-native"         % "3.5.0"
  val kxbmapConfigsDep:  ModuleID = "com.github.kxbmap"          %% "configs"               % "0.4.4"
  val mongodbDep:        ModuleID = "org.mongodb"                %  "mongodb-driver"        % "3.4.0"
  val nScalaTimeDep:     ModuleID = "com.github.nscala-time"     %% "nscala-time"           % "2.14.0"
  val reflectionsDep:    ModuleID = "org.reflections"            %  "reflections"           % "0.9.10"
  val scalaLoggingDep:   ModuleID = "com.typesafe.scala-logging" %% "scala-logging"         % "3.5.0"
  val scalaTestDep:      ModuleID = "org.scalatest"              %% "scalatest"             % "3.0.1"
  val slf4jSimpleDep:    ModuleID = "org.slf4j"                  %  "slf4j-simple"          % "1.7.21"
  val sqliteDep:         ModuleID = "org.xerial"                 %  "sqlite-jdbc"           % "3.16.1"
  val typesafeConfigDep: ModuleID = "com.typesafe"               %  "config"                % "1.3.1"

}
