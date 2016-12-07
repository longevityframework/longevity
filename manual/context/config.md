---
title: configuring your longevity context
layout: page
---

Longevity uses [Typesafe
Config](https://github.com/typesafehub/config) to help you configure
your `LongevityContext`. The default configuration settings are found
in the `reference.conf` file included in the longevity jar. (Here's
the [latest version on
GitHub](https://github.com/longevityframework/longevity/blob/master/longevity/src/main/resources/reference.conf).)

The typical way to supply configuration to your application is to
override these defaults in your `application.conf` file, located on
your classpath.

If you have multiple `LongevityContexts` living in the same
application, and they have different configurations, you can supply
separate `com.typesafe.config.Config` objects to the
`LongevityContext` factory method:

```scala
import com.typesafe.config.Config
import longevity.context.LongevityContext
import longevity.subdomain.Subdomain

val bloggingDomain = Subdomain("com.example.app.model.blogging")
val bloggingConfig: Config = loadBloggingConfig()
val bloggingContext = LongevityContext(
  bloggingDomain,
  bloggingConfig)

val accountsSubdomain = Subdomain("com.example.app.model.accounts")
val accountsConfig: Config = loadAccountsConfig()
val accountsContext = LongevityContext(
  accountsSubdomain,
  accountsConfig)
```

Please see the [Typesafe Config
documentation](https://github.com/typesafehub/config#overview) for
more information on the different ways you can manage your
configuration.

Longevity converts the Typesafe Config into a `LongevityConfig` case
class internally. You can use case class configuration if you
prefer. Here we use the `LongevityConfig` case class to define the
same configuration as found in the `reference.conf` file:

```scala
import longevity.context.InMem
import longevity.context.LongevityConfig
import longevity.context.MongoConfig
import longevity.context.TestConfig
import longevity.context.CassandraConfig

val longevityConfig = LongevityConfig(
  backEnd = InMem, // one of InMem, Mongo, Cassandra
  autocreateSchema = false,
  optimisticLocking = false,
  mongodb = MongoConfig(
    uri = "mongodb://127.0.0.1:27017",
    db = "longevity_main"),
  cassandra = CassandraConfig(
    address = "127.0.0.1",
    credentials = None,
    keyspace = "longevity_main",
    replicationFactor = 1),
  test = TestConfig(
    mongodb = MongoConfig(
      uri = "mongodb://127.0.0.1:27017",
      db = "longevity_test"),
    cassandra = CassandraConfig(
      address = "127.0.0.1",
      credentials = None,
      keyspace = "longevity_test",
      replicationFactor = 1)))

val bloggingContext = new LongevityContext(
  bloggingDomain,
  longevityConfig)
```

The most important configuration setting is `longevity.backEnd`. This
is where you choose your database. Right now, the options are `InMem`,
`Mongo`, and `Cassandra`.

{% assign prevTitle = "the longevity context" %}
{% assign prevLink = "." %}
{% assign upTitle = "the longevity context" %}
{% assign upLink = "." %}
{% assign nextTitle = "optimistic locking" %}
{% assign nextLink = "opt-lock.html" %}
{% include navigate.html %}

