
lazy val root = Project(id = "root", base = file("."), settings = BuildSettings.noPublishSettings)
  .aggregate(bin, emblem, longevity, longevityMongoDeps, longevityCassandraDeps)

lazy val bin = Project(id = "bin", base = file("bin"), settings = BuildSettings.noPublishSettings)

lazy val longevity = Project(
  id = "longevity",
  base = file("longevity"),
  settings = BuildSettings.buildSettings ++ Seq(

    // non-optional library dependencies:
    libraryDependencies += Dependencies.typesafeConfigDep,
    libraryDependencies += Dependencies.kxbmapConfigsDep,
    libraryDependencies += Dependencies.scalaLoggingDep,

    // optional library dependencies:
    libraryDependencies += Dependencies.scalaTestDep % Optional,
    libraryDependencies += Dependencies.json4sDep % Optional,
    libraryDependencies += Dependencies.akkaStreamDep % Optional,

    // test dependencies:
    libraryDependencies += Dependencies.slf4jSimpleDep % Test,
    libraryDependencies += Dependencies.json4sDep % Test,
    libraryDependencies += Dependencies.akkaStreamDep % Test,

    // for mongo:
    libraryDependencies += Dependencies.mongodbDep % Optional,
    libraryDependencies += Dependencies.mongodbDep % Test,

    // for cassandra:
    libraryDependencies += Dependencies.cassandraDep % Optional,
    libraryDependencies += Dependencies.cassandraDep % Test,
    libraryDependencies += Dependencies.json4sDep % Optional,
    libraryDependencies += Dependencies.json4sDep % Test,

    homepage := BuildSettings.longevityHomepage,
    pomExtra := BuildSettings.longevityPomExtra,

    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  )
)
.dependsOn(emblem)

lazy val emblem = Project(
  id = "emblem",
  base = file("emblem"),
  settings = BuildSettings.buildSettings ++ Seq(
    libraryDependencies += Dependencies.json4sDep % Optional,
    homepage := Some(url("https://github.com/longevityframework/emblem")),
    pomExtra := (
      <scm>
      <url>git@github.com:longevityframework/emblem.git</url>
      <connection>scm:git:git@github.com:longevityframework/emblem.git</connection>
      </scm>
      <developers>
      <developer>
      <id>sullivan-</id>
      <name>John Sullivan</name>
      <url>https://github.com/sullivan-</url>
      </developer>
      </developers>)
  )
)

lazy val longevityMongoDeps = Project(
  id = "longevity-mongo-deps",
  base = file("longevity-mongo-deps"),
  settings = BuildSettings.publishSettings ++ Seq(
    libraryDependencies += Dependencies.mongodbDep,
    homepage := BuildSettings.longevityHomepage,
    pomExtra := BuildSettings.longevityPomExtra))

lazy val longevityCassandraDeps = Project(
  id = "longevity-cassandra-deps",
  base = file("longevity-cassandra-deps"),
  settings = BuildSettings.publishSettings ++ Seq(
    libraryDependencies += Dependencies.cassandraDep,
    libraryDependencies += Dependencies.json4sDep,
    homepage := BuildSettings.longevityHomepage,
    pomExtra := BuildSettings.longevityPomExtra))
