---
title: configuring your longevity context
layout: page
---

Longevity uses [Typesafe
Config](https://github.com/typesafehub/config) to help you configure
your `LongevityContext`. The default configuration settings are found
in the `reference.conf` file included in the longevity jar. (Here's
the [latest version on
GitHub](https://github.com/longevityframework/longevity/blob/master/src/main/resources/reference.conf).)
This file contains settings for all the available context
configurations.

The typical way to supply configuration to you application is to
override these defaults in your `application.conf` file, located in
your classpath.

If you have multiple `LongevityContexts` living in the same
application, and they have different configurations, you can supply
separate `com.typesafe.config.Config` objects to the
`LongevityContext` factory method:

{% gist sullivan-/dc8f510232feb9819f08 %}

Please see the [Typesafe Config
documentation](https://github.com/typesafehub/config#overview) for
more information on the different ways you can manage your
configuration.

{% assign prevTitle = "todo" %}
{% assign prevLink = "todo.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = "./" %}
{% assign nextTitle="todo" %}
{% assign nextLink="todo.html" %}
{% include navigate.html %}

