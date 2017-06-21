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

It's possible to have multiple domain models in the same application, and to supply the
`LongevityContexts` for these models with different configurations. For instance, suppose we have a
core domain in our blogging application:

```scala
package bloggingapp.core

import longevity.model.annotations.domainModel

package object model {

  @domainModel trait BloggingDomain

}
```

And a supporting subdomain for managing accounts:

```scala
package bloggingapp.accounts

import longevity.model.annotations.domainModel

package object model {

  @domainModel trait AccountsSubdomain

}
```

You can supply separate `com.typesafe.config.Config` objects to each
`LongevityContext` like so:

```scala
import bloggingapp.accounts.model.AccountsSubdomain
import bloggingapp.core.model.BloggingDomain
import com.typesafe.config.Config
import longevity.context.LongevityContext

val bloggingConfig: Config = loadBloggingConfig()
val bloggingContext = LongevityContext[BloggingDomain](bloggingConfig)

val accountsConfig: Config = loadAccountsConfig()
val accountsContext = LongevityContext[AccountsSubdomain](accountsConfig)
```

Please see the [Typesafe Config documentation](https://github.com/typesafehub/config#overview) for
more information on the different ways you can manage your configuration.

Longevity converts the Typesafe Config into a `LongevityConfig` case
class internally. You can use case class configuration if you
prefer. Here we use the `LongevityConfig` case class to define the
same configuration as found in the `reference.conf` file:

```scala
import longevity.context.CassandraConfig
import longevity.context.InMem
import longevity.context.JdbcConfig
import longevity.context.LongevityConfig
import longevity.context.MongoConfig
import longevity.context.TestConfig

val longevityConfig = LongevityConfig(
  backEnd = InMem, // one of Cassandra, InMem, JDBC, MongoDB, SQLite
  autoOpenConnection = false,
  autoCreateSchema = false,
  optimisticLocking = false,
  writeTimestamps = false,
  cassandra = CassandraConfig(
    autoCreateKeyspace = false,
    address = "127.0.0.1",
    credentials = None,
    keyspace = "longevity_main",
    replicationFactor = 1),
  mongodb = MongoConfig(
    uri = "mongodb://127.0.0.1:27017",
    db = "longevity_main"),
  jdbc = JdbcConfig(
    driverClass = "org.sqlite.JDBC"
    url = "jdbc:sqlite:longevity_main.db"),
  test = TestConfig(
    cassandra = CassandraConfig(
      autoCreateKeyspace = false,
      address = "127.0.0.1",
      credentials = None,
      keyspace = "longevity_test",
      replicationFactor = 1),
    mongodb = MongoConfig(
      uri = "mongodb://127.0.0.1:27017",
      db = "longevity_test"),
    jdbc = JdbcConfig(
      driverClass = "org.sqlite.JDBC"
      url = "jdbc:sqlite:longevity_test.db")))

val bloggingContext = new LongevityContext[BloggingDomain](longevityConfig)
```

The most important configuration setting is `longevity.backEnd`. This
is where you choose your database. Right now, the options are
`Cassandra`, `JDBC`, `InMem`, `MongoDB`, and `SQLite`.

The `JDBC` back end is a generic back end that you can use for any
databases with a JDBC driver. Unfortunately, we are not able to
provide support for any JDBC driver other than SQLite, because we have
no way of writing integration tests against this back end without
specifying the JDBC driver, and the longevity framework team (i.e.,
me) already has their hands full supporting the other back ends. That
said, we would be *more* than happy to give you help and advice in
troubleshooting any problems. In fact, we've made it as easy as
possible for you to build and maintain your own JDBC-flavored back
end. See [these
instructions](https://github.com/longevityframework/longevity/wiki/How-to-create-a-new-JDBC-back-end)
for details.

{% assign prevTitle = "the longevity context" %}
{% assign prevLink  = "." %}
{% assign upTitle   = "the longevity context" %}
{% assign upLink    = "." %}
{% assign nextTitle = "repositories in the context" %}
{% assign nextLink  = "repos.html" %}
{% include navigate.html %}
