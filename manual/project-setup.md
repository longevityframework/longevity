---
title: project setup
layout: page
---

## Preliminarys - Scala version

We're currently only publishing artifacts for Scala 2.11.x, so be sure
your project is using a 2.11 version of Scala. For example, your
`build.sbt` file may have:

    scalaVersion := "2.11.7"

## Using Sonatype Artifacts

Include the following two lines in your `build.sbt` to declare the dependency:

    resolvers += Resolver.sonatypeRepo("releases")

    libraryDependencies += "org.longevityframework" %% "longevity" % "0.4.1"

## Building the Artifacts Yourself

### Download the Source

The source code for longevity, and for dependency
[emblem](https://github.com/sullivan-/emblem/wiki), is [housed in the
longevity project on
GitHub](https://github.com/sullivan-/longevity). To use it, first
create a clone of that project:

    git clone https://github.com/sullivan-/longevity.git
    cd longevity

### Choose the Right Branch

You probably want to be on the `master` branch, as this holds the
latest working version. You are probably already there, but just in
case:

    git checkout master
    git pull

### Publish Local

Now use SBT to publish the `emblem` and `longevity` artifacts
locally. From the command line:

    sbt publishLocal

### Include as a Library Dependency

In the projects where you want to use longevity, include a library
dependency. If you are on the `master` branch, use:

    libraryDependencies += "org.longevityframework" %% "longevity" % "0.5-SNAPSHOT"

{% assign prevTitle = "aggregates and entities" %}
{% assign prevLink = "ddd-basics/aggregates-and-entities.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = "./" %}
{% assign nextTitle="building your subdomain" %}
{% assign nextLink="subdomain/" %}
{% include navigate.html %}

