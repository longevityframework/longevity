---
title: configuring your longevity context
layout: page
---

Longevity uses [Typesafe
Config](https://github.com/typesafehub/config) to help you configure
your `LongevityContext`. The default configuration settings are found
in the `reference.conf` file included in the longevity jar.  This file
contains settings for all the available context
configurations. (Here's the [latest version on
GitHub](https://github.com/longevityframework/longevity/blob/master/src/main/resources/reference.conf).)

The typical way to supply configuration to your application is to
override these defaults in your `application.conf` file, located on
your classpath.

If you have multiple `LongevityContexts` living in the same
application, and they have different configurations, you can supply
separate `com.typesafe.config.Config` objects to the
`LongevityContext` factory method:

```scala
import com.typesafe.config.Config
import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.CoreDomain
import longevity.subdomain.EntityTypePool
import longevity.subdomain.SupportingSubdomain

val bloggingDomain: CoreDomain =
  CoreDomain("blogging", EntityTypePool.empty)
val bloggingConfig: Config = loadBloggingConfig()
val bloggingContext = LongevityContext(
  bloggingDomain,
  Mongo,
  config = bloggingConfig)

val accountsSubdomain: SupportingSubdomain =
  SupportingSubdomain("accounts", EntityTypePool.empty)
val accountsConfig: Config = loadAccountsConfig()
val accountsContext = LongevityContext(
  accountsSubdomain,
  Cassandra,
  config = accountsConfig)
```

Please see the [Typesafe Config
documentation](https://github.com/typesafehub/config#overview) for
more information on the different ways you can manage your
configuration.

{% assign prevTitle = "persistence strategy" %}
{% assign prevLink = "pstrat.html" %}
{% assign upTitle = "the longevity context" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo pools" %}
{% assign nextLink = "repo-pools.html" %}
{% include navigate.html %}

