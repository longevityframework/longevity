---
title: building the longevity context
layout: page
---

Once we have your domain model in place, we are ready to build our
`LongevityContext`, which we do in `SimblContextImpl.scala`:

```scala
import longevity.context.LongevityContext
import simbl.domain.SimblDomainModel

val longevityContext = LongevityContext[SimblDomainModel]()
```

The longevity context provides a variety of tools that are tailored to your model. The most
important of these is the `Repo`, which you can use to do standard CRUD operations
(create/retrieve/update/delete) with your persistent objects, as well as executing queries that
return more than one result.

Longevity uses [Typesafe
Config](https://github.com/typesafehub/config) to configure the
longevity context. Typically, the configuration is drawn from the
`application.conf` resource file. The longevity configuration for
Simple Blogging looks this:

```prop
longevity.backEnd = InMem
longevity.autocreateSchema = true
longevity.optimisticLocking = false

longevity.mongodb.uri = "localhost:27017"
longevity.mongodb.db = simbl_main
longevity.test.mongodb.uri = "localhost:27017"
longevity.test.mongodb.db = simbl_test

longevity.cassandra.address = "localhost"
longevity.cassandra.useCredentials = no
longevity.cassandra.username = nil
longevity.cassandra.password = nil
longevity.cassandra.keyspace = simbl_main
longevity.cassandra.replicationFactor = 1

longevity.test.cassandra.address = "localhost"
longevity.test.cassandra.useCredentials = no
longevity.test.cassandra.username = nil
longevity.test.cassandra.password = nil
longevity.test.cassandra.keyspace = simbl_test
longevity.test.cassandra.replicationFactor = 1
```

Here, you need to specify the back end in configuration property
`longevity.backEnd`. Your choices are currently `Cassandra`, `InMem`,
`Mongo`, and `SQLite`. We use `InMem` out of the box. If you want to
try Cassandra or MongoDB, you will need to set up a database system to
connect to. The SQLite back end will work without any extra setup, as
all you need to run SQLite is the right jar on your classpath.

You can find configurations for main and test databases for the
various back ends in the config file. If you want to experiment with
adjusting the persistence strategy to use a real database, you may
need to adjust this configuration.

{% assign prevTitle = "username and email" %}
{% assign prevLink  = "keyvals.html" %}
{% assign upTitle   = "getting started guide" %}
{% assign upLink    = "." %}
{% assign nextTitle = "the akka http routes" %}
{% assign nextLink  = "routes.html" %}
{% include navigate.html %}
