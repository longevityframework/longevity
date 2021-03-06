import sbt._
import Keys._

object Dependencies {

  val scalaVersionString = "2.12.6"

  val akkaStreamDep:          ModuleID = "com.typesafe.akka"          %% "akka-stream"                 % "2.5.13"
  val cassandraDep:           ModuleID = "com.datastax.cassandra"     %  "cassandra-driver-core"       % "3.5.0"
  val catsDep:                ModuleID = "org.typelevel"              %% "cats-core"                   % "1.1.0"
  val catsEffectDep:          ModuleID = "org.typelevel"              %% "cats-effect"                 % "0.10.1"
  val catsIterateeDep:        ModuleID = "io.iteratee"                %% "iteratee-core"               % "0.17.0"
  val fs2CoreDep:             ModuleID = "co.fs2"                     %% "fs2-core"                    % "0.10.4"
  val journalDep:             ModuleID = {
    "io.verizon.journal" %% "core" % "3.0.19" exclude ("ch.qos.logback", "logback-classic")
  }
  val json4sDep:              ModuleID = "org.json4s"                 %% "json4s-native"               % "3.5.3"
  val kxbmapConfigsDep:       ModuleID = "com.github.kxbmap"          %% "configs"                     % "0.4.4"
  val mongodbDep:             ModuleID = "org.mongodb"                %  "mongodb-driver"              % "3.8.0"
  val nScalaTimeDep:          ModuleID = "com.github.nscala-time"     %% "nscala-time"                 % "2.20.0"
  val playIterateeDep:        ModuleID = "com.typesafe.play"          %% "play-iteratees"              % "2.6.1"
  val scalaTestDep:           ModuleID = "org.scalatest"              %% "scalatest"                   % "3.0.5"
  val scalacheckDatetimeDep:  ModuleID = "com.47deg"                  %% "scalacheck-toolbox-datetime" % "0.2.5"
  val scalacheckDep:          ModuleID = "org.scalacheck"             %% "scalacheck"                  % "1.14.0"
  val scalacheckShapelessDep: ModuleID = "com.github.alexarchambault" %% "scalacheck-shapeless_1.13"   % "1.1.6"
  val shapelessDep:           ModuleID = "com.chuusai"                %% "shapeless"                   % "2.3.3"
  val slf4jSimpleDep:         ModuleID = "org.slf4j"                  %  "slf4j-simple"                % "1.7.25"
  val sqliteDep:              ModuleID = "org.xerial"                 %  "sqlite-jdbc"                 % "3.23.1"
  val streamAdapterDep:       ModuleID = "org.longevityframework"     %% "streamadapter"               % "0.2.0"
  val typekeyDep:             ModuleID = "org.longevityframework"     %% "typekey"                     % "1.0.1"
  val typesafeConfigDep:      ModuleID = "com.typesafe"               %  "config"                      % "1.3.3"

}
