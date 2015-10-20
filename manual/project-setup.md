---
title: project setup
layout: page
---

I'm currently only publishing artifacts for Scala 2.11.x, so be sure
your project is using a 2.11 version of Scala. For example, your
`build.sbt` file may have:

    scalaVersion := "2.11.7"

Include the following two lines in your `build.sbt` to declare the dependency:

    resolvers += Resolver.sonatypeRepo("releases")

    libraryDependencies += "net.jsmscs" %% "longevity" % "0.1.3"

{% assign prevTitle = "aggregates and entities" %}
{% assign prevLink = "ddd-basics/aggregates-and-entities.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = "./" %}
{% assign nextTitle="building your subdomain" %}
{% assign nextLink="subdomain/" %}
{% include navigate.html %}

