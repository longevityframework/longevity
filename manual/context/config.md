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
import longevity.subdomain.SupportingSubdomain
import longevity.subdomain.ptype.PTypePool

val bloggingDomain: CoreDomain =
  CoreDomain("blogging", PTypePool.empty)
val bloggingConfig: Config = loadBloggingConfig()
val bloggingContext = LongevityContext(
  bloggingDomain,
  Mongo,
  config = bloggingConfig)

val accountsSubdomain: SupportingSubdomain =
  SupportingSubdomain("accounts", PTypePool.empty)
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

Longevity converts the Typesafe Config into a `LongevityConfig` case
class internally. You can use case class configuration if you
prefer. Just use the `LongevityContext` constructor directly, instead
of the `LongevityContext.apply` factory method used above. Here we use
the `LongevityConfig` case class to define the same configuration as
found in the `reference.conf` file:

```scala
import longevity.context.LongevityConfig
import longevity.context.MongoConfig
import longevity.context.TestConfig
import longevity.context.CassandraConfig

val longevityConfig = LongevityConfig(
  optimisticLocking = false,
  mongodb = MongoConfig(
    uri = "localhost:27017",
    db = "longevity_main"),
  cassandra = CassandraConfig(
    address = "localhost",
    credentials = None,
    keyspace = "longevity_main",
    replicationFactor = 1),
  test = TestConfig(
    mongodb = MongoConfig(
      uri = "localhost:27017",
      db = "longevity_test"),
    cassandra = CassandraConfig(
      address = "localhost",
      credentials = None,
      keyspace = "longevity_test",
      replicationFactor = 1)))

val bloggingContext = new LongevityContext(
  bloggingDomain,
  Mongo,
  config = longevityConfig)
```

{% assign prevTitle = "persistence strategy" %}
{% assign prevLink = "pstrat.html" %}
{% assign upTitle = "the longevity context" %}
{% assign upLink = "." %}
{% assign nextTitle = "optimistic locking" %}
{% assign nextLink = "opt-lock.html" %}
{% include navigate.html %}

