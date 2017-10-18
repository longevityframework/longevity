version      := "0.0"
scalaVersion := "2.12.2"
modelPackage := "simple"
migrationsPackage := "simple"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

enablePlugins(longevity.migrations.Plugin)

libraryDependencies += "org.longevityframework" %% "longevity"             % sys.props("plugin.version")
libraryDependencies += "org.longevityframework" %% "longevity-migrations"  % sys.props("plugin.version")
libraryDependencies += "org.longevityframework" %% "longevity-sqlite-deps" % sys.props("plugin.version")
