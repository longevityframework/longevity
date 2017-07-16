
lazy val root = Project(id = "root", base = file("."))
  .settings(BuildSettings.noPublishSettings)
  .aggregate(bin,
    longevity,
    longevityCassandraDeps,
    longevityMongoDbDeps,
    longevitySqliteDeps,
    longevityMigrations)

lazy val bin = Project(id = "bin", base = file("bin"))
  .settings(BuildSettings.noPublishSettings)

lazy val longevity = project.in(file("longevity"))
  .settings(BuildSettings.buildSettings: _*)
  .settings(
    libraryDependencies += Dependencies.journalDep,
    libraryDependencies += Dependencies.kxbmapConfigsDep,
    libraryDependencies += Dependencies.scalacheckDatetimeDep,
    libraryDependencies += Dependencies.scalacheckDep,
    libraryDependencies += Dependencies.scalacheckShapelessDep,
    libraryDependencies += Dependencies.shapelessDep,
    libraryDependencies += Dependencies.streamAdapterDep,
    libraryDependencies += Dependencies.typekeyDep,
    libraryDependencies += Dependencies.typesafeConfigDep,

    libraryDependencies += Dependencies.slf4jSimpleDep  % "test",

    libraryDependencies += Dependencies.akkaStreamDep   % "optional,test",
    libraryDependencies += Dependencies.cassandraDep    % "optional,test",
    libraryDependencies += Dependencies.catsDep         % "optional,test",
    libraryDependencies += Dependencies.catsEffectDep   % "optional,test",
    libraryDependencies += Dependencies.catsIterateeDep % "optional,test",
    libraryDependencies += Dependencies.fs2CoreDep      % "optional,test",
    libraryDependencies += Dependencies.json4sDep       % "optional,test",
    libraryDependencies += Dependencies.mongodbDep      % "optional,test",
    libraryDependencies += Dependencies.playIterateeDep % "optional,test",
    libraryDependencies += Dependencies.scalaTestDep    % "optional,test",
    libraryDependencies += Dependencies.sqliteDep       % "optional,test",

    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"))
  .enablePlugins(JekyllPlugin, SiteScaladocPlugin, GhpagesPlugin)
  .settings(
    git.remoteRepo := "git@github.com:longevityframework/longevity.git",
    includeFilter in Jekyll :=
      ("*.html" | "*.png" | "*.js" | "*.css" | "*.gif" | "CNAME" | ".nojekyll" | "*.json" | "*.jpg"),
    siteSubdirName in SiteScaladoc := "api")

lazy val longevityCassandraDeps = Project("longevity-cassandra-deps", file("longevity-cassandra-deps"))
  .settings(BuildSettings.publishSettings)
  .settings(
    libraryDependencies += Dependencies.cassandraDep,
    libraryDependencies += Dependencies.json4sDep)

lazy val longevityMongoDbDeps = Project("longevity-mongodb-deps", file("longevity-mongodb-deps"))
  .settings(BuildSettings.publishSettings)
  .settings(
    libraryDependencies += Dependencies.mongodbDep)

lazy val longevitySqliteDeps = Project("longevity-sqlite-deps", file("longevity-sqlite-deps"))
  .settings(BuildSettings.publishSettings)
  .settings(
    libraryDependencies += Dependencies.sqliteDep,
    libraryDependencies += Dependencies.json4sDep)

lazy val longevityMigrations = Project("longevity-migrations", file("longevity-migrations"))
  .settings(BuildSettings.buildSettings)
  .settings(
    libraryDependencies += Dependencies.catsDep,
    libraryDependencies += Dependencies.catsIterateeDep,
    libraryDependencies += Dependencies.journalDep,
    libraryDependencies += Dependencies.cassandraDep   % "test",
    libraryDependencies += Dependencies.json4sDep      % "test",
    libraryDependencies += Dependencies.mongodbDep     % "test",
    libraryDependencies += Dependencies.slf4jSimpleDep % "test",
    libraryDependencies += Dependencies.sqliteDep      % "test",
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"))
  .dependsOn(longevity)
