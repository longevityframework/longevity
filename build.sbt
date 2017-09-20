
lazy val root = Project(id = "root", base = file("."))
  .settings(BuildSettings.noPublishSettings)
  .aggregate(bin,
    longevity,
    longevityCassandraDeps,
    longevityMongoDbDeps,
    longevitySqliteDeps,
    longevityMigrationsPlugin)

lazy val bin = Project(id = "bin", base = file("bin"))
  .settings(BuildSettings.noPublishSettings)

lazy val longevity = project.in(file("longevity"))
  .settings(BuildSettings.buildSettings: _*)
  .settings(
    libraryDependencies += Dependencies.journalDep,
    libraryDependencies += Dependencies.kxbmapConfigsDep,
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

    homepage := BuildSettings.longevityHomepage,
    pomExtra := BuildSettings.longevityPomExtra,

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
    libraryDependencies += Dependencies.json4sDep,
    homepage := BuildSettings.longevityHomepage,
    pomExtra := BuildSettings.longevityPomExtra)

lazy val longevityMongoDbDeps = Project("longevity-mongodb-deps", file("longevity-mongodb-deps"))
  .settings(BuildSettings.publishSettings)
  .settings(
    libraryDependencies += Dependencies.mongodbDep,
    homepage := BuildSettings.longevityHomepage,
    pomExtra := BuildSettings.longevityPomExtra)

lazy val longevitySqliteDeps = Project("longevity-sqlite-deps", file("longevity-sqlite-deps"))
  .settings(BuildSettings.publishSettings)
  .settings(
    libraryDependencies += Dependencies.sqliteDep,
    homepage := BuildSettings.longevityHomepage,
    pomExtra := BuildSettings.longevityPomExtra)

lazy val longevityMigrationsPlugin = Project("sbt-longevity-migrations", file("sbt-longevity-migrations"))
  .settings(BuildSettings.publishSettings)
  .settings(ScriptedPlugin.scriptedSettings)
  .settings(
      sbtPlugin := true,
      scriptedBufferLog := false,
      scriptedLaunchOpts += "-Dplugin.version=" + version.value,
      libraryDependencies += Dependencies.scalaTestDep % "test",
      homepage := BuildSettings.longevityHomepage,
      pomExtra := BuildSettings.longevityPomExtra)
