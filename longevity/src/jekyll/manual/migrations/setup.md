---
title: project setup for longevity migrations
layout: page
---

In addition to the library dependencies you [set up](../prelims/project-setup.html) for longevity
proper, you will need to add another library dependency to your `build.sbt` for migrations:

```scala
libraryDependencies += "org.longevityframework" %% "longevity-migrations" % "0.26.0"
```

You will also want to add the `sbt-longevity-migrations` plugin to your build by adding the
following line to `project/plugins.sbt`:

```scala
addSbtPlugin("org.longevityframework" % "sbt-longevity-migrations" % "0.1.0")
```

The `longevity-migrations` library dependency will move in lock-step with the other longevity
artifacts. However, the `sbt-longevity-migrations` plugin maintains its own version and release
cycle.

Enable the SBT plugin back in your `build.sbt`:

```scala
enablePlugins(longevity.migrations.Plugin)
```

The plugin requires the following two settings to be added to your build:

```scala
modelPackage := "com.example.domain"
migrationsPackage := "com.example.migrations"
```

The `modelPackage` is the package where your domain model classes are found. The `migrationsPackage`
is a package to contain your migrations and your model tags.

The code for these packages is assumed to live in the standard locations. In the example above,
the plugin will look for model code in `src/main/scala/com/example/domain`, and migrations code in
`src/main/scala/com/example/migrations`. You can override these locations if you like with settings
`modelSourceDir` and `migrationsSourceDir`. For instance, if you leave out the `com` and `example`
directories in the middle, you could specify this like so:

```scala
modelSourceDir := (scalaSource in Compile).value / "domain"
migrationsSourceDir := (scalaSource in Compile).value / "migrations"
```

{% assign prevTitle = "migrating to a new version of your domain" %}
{% assign prevLink  = "." %}
{% assign upTitle   = "migrating to a new version of your domain" %}
{% assign upLink    = "." %}
{% assign nextTitle = "tagging a version of your domain model" %}
{% assign nextLink  = "tagging.html" %}
{% include navigate.html %}
