---
title: project setup
layout: page
---

## scala version

We currently publish artifacts for Scala versions 2.11 and 2.12, so be
sure your project is using a compatible Scala version. For example,
your `build.sbt` file may have:

```scala
scalaVersion := "2.11.8"
```

or:

```scala
scalaVersion := "2.12.1"
```

## using sonatype artifacts

Include the following two lines in your `build.sbt` to declare the dependency:

```scala
resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies += "org.longevityframework" %% "longevity" % "0.19.0"
```

Include one of the following lines to bring in the library
dependencies for the back end of your choice:

```scala
libraryDependencies += "org.longevityframework" %% "longevity-cassandra-deps" % "0.19.0"

libraryDependencies += "org.longevityframework" %% "longevity-mongo-deps" % "0.19.0"
```

## enabling macro annotations

You will probably want to use the macro annotations provided in
package `longevity.model.annotations`. To do so, you will need to
add [Macro
Paradise](http://docs.scala-lang.org/overviews/macros/paradise.html)
to your build, like so:

```scala
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
```

## building the artifacts yourself

### download the source

The source code for longevity, and for dependency
[emblem](https://github.com/longevityframework/emblem/wiki), is [housed in the
longevity project on
GitHub](https://github.com/longevityframework/longevity). To use it, first
create a clone of that project:

```bash
% git clone https://github.com/longevityframework/longevity.git
% cd longevity
```

### choose the right branch

You probably want to be on the `master` branch, as this holds the
latest working version. You are probably already there, but just in
case:

```bash
% git checkout master
% git pull
```

### publish local

Now use SBT to publish the `emblem` and `longevity` artifacts
locally. From the command line:

```bash
% sbt publishLocal
```

### include as a library dependency

In the projects where you want to use longevity, include a library
dependency. If you are on the `master` branch, use:

```scala
libraryDependencies += "org.longevityframework" %% "longevity" % "0.20-SNAPSHOT"
```

{% assign prevTitle = "what is longevity" %}
{% assign prevLink  = "what-is-longevity.html" %}
{% assign upTitle   = "preliminaries" %}
{% assign upLink    = "." %}
{% assign nextTitle = "the domain model" %}
{% assign nextLink  = "../model/index.html" %}
{% include navigate.html %}
