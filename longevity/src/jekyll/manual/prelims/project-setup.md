---
title: project setup
layout: page
---

## scala version

We currently publish artifacts for Scala versions 2.11 and 2.12, so be
sure your project is using a compatible Scala version. For example,
your `build.sbt` file may have:

```scala
scalaVersion := "2.11.12"
```

or:

```scala
scalaVersion := "2.12.6"
```

## using sonatype artifacts

Include the following two lines in your `build.sbt` to declare the dependency:

```scala
resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies += "org.longevityframework" %% "longevity" % "0.27.0"
```

Include one of the following lines to bring in the library
dependencies for the back end of your choice:

```scala
libraryDependencies += "org.longevityframework" %% "longevity-cassandra-deps" % "0.27.0"

libraryDependencies += "org.longevityframework" %% "longevity-mongodb-deps" % "0.27.0"

libraryDependencies += "org.longevityframework" %% "longevity-sqlite-deps" % "0.27.0"
```

All longevity artifact versions move in lock-step, so it can be convenient to store it in a
variable, like so:

```scala
val longevityVersion = "0.27.0"
libraryDependencies += "org.longevityframework" %% "longevity"                % longevityVersion
libraryDependencies += "org.longevityframework" %% "longevity-cassandra-deps" % longevityVersion
libraryDependencies += "org.longevityframework" %% "longevity-migrations"     % longevityVersion
```

## enabling macro annotations

You will most likely want to use the macro annotations provided in package
`longevity.model.annotations`. To do so, you will need to add [Macro
Paradise](http://docs.scala-lang.org/overviews/macros/paradise.html) to your build, like so:

```scala
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
```

## supplying optional dependencies

There are a number of optional longevity features that require you to bring in other library
dependencies yourself. If you want to use `cats.effect.IO` as an effect, you will need
[cats-effect](https://github.com/typelevel/cats-effect):

```scala
libraryDependencies += "org.typelevel" %% "cats-effect" % "0.10.1"
```

If you want to [stream query results](../query/stream-by.html) to [Akka
Streams](http://doc.akka.io/docs/akka/2.4.17/scala/stream/index.html):

```scala
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.12"
```

If you want to [stream query results](../query/stream-by.html) to
[FS2](https://github.com/functional-streams-for-scala/fs2):

```scala
libraryDependencies += "co.fs2" %% "fs2-core" % "0.10.4"
```

If you want to [stream query results](../query/stream-by.html) to
[iteratee.io](https://github.com/travisbrown/iteratee):

```scala
libraryDependencies += "io.iteratee" %% "iteratee-core" % "0.17.0"
```

If you want to [stream query results](../query/stream-by.html) to
[Play enumerators](https://www.playframework.com/documentation/2.5.x/Enumerators):

```scala
libraryDependencies += "com.typesafe.play" %% "play-iteratees" % "2.6.1"
```

If you want to use `longevity.test.RepoCrudSpec` or `longevity.test.QuerySpec` to [build integration
tests](../testing), you will need [ScalaTest](http://www.scalatest.org/):

```scala
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5"
```

{% assign prevTitle = "what is longevity" %}
{% assign prevLink  = "what-is-longevity.html" %}
{% assign upTitle   = "preliminaries" %}
{% assign upLink    = "." %}
{% assign nextTitle = "the domain model" %}
{% assign nextLink  = "../model/index.html" %}
{% include navigate.html %}
