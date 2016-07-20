---
title: building the longevity context
layout: page
---

Once we have your subdomain in place, we are ready to build our
`LongevityContext`:

```scala
import longevity.context.LongevityContext
import longevity.context.InMem

val coreDomain = new SimblCoreDomain
val longevityContext = LongevityContext(coreDomain, InMem)
```

The longevity context provides a variety of tools that are tailored to
your subdomain. The most important of these is the `RepoPool`, which
contains repositories for your persistent objects. You can use these
repositories to do standard CRUD operations
(create/retrieve/update/delete), as well as executing queries that
return more than one result.

Apart from the subdomain itself, we have to provide a
`PersistenceStrategy` when building the longevity context. Your
choices are currently `InMem`, `Mongo`, and `Cassandra`. We use
`InMem` out of the box, so that this tutorial will work even if you
don't have a MongoDB or Cassandra database set up for use.

Longevity uses [Typesafe
Config](https://github.com/typesafehub/config) to configure the
longevity context. Typically, the configuration is drawn from the
`application.conf` resource file:

```prop
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

Here, you can find configurations for main and test databases for both
Mongo and Cassandra. If you want to experiment with adjusting the
persistence strategy to use a real database, you may need to tweak
this configuration.

{% assign prevTitle = "building the subdomain" %}
{% assign prevLink = "building.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="the akka http routes" %}
{% assign nextLink="routes.html" %}
{% include navigate.html %}
