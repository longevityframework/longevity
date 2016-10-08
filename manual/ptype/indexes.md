---
title: indexes
layout: page
---

An index in longevity is a requirement that certain forms of queries
on our persistent objects should perform with low latency. For
instance, if there is an expectation that queries on `lastName` /
`firstName` should perform quickly, then we need to define an
index. To do this, we declare our index in a singleton object
`indexes` inside our `PType`:

```scala
import longevity.subdomain.KeyVal
import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class Username(username: String) extends KeyVal[User, Username]

case class User(
  username: Username,
  firstName: String,
  lastName: String)
extends Persistent

object User extends PType[User] {
  object props {
    val username = prop[Username]("username")
    val firstName = prop[String]("firstName")
    val lastName = prop[String]("lastName")
  }
  object keys {
    val username = key(props.username)
  }
  object indexes {
    val fullname = index(props.lastName, props.firstName)
  }
}
```

An index like `User.indexes.fullname` above will assure fast
performance for queries that filter on `lastName`, as well as for
queries where `lastName` is fixed and `firstName` is filtered. It will
not assure performance for a search on `firstName` alone.

Indexes are used by `Repo.retrieveByQuery`, which is described in a [later
section](../repo/query.html).

{% assign prevTitle = "keys" %}
{% assign prevLink = "keys.html" %}
{% assign upTitle = "the persistent type" %}
{% assign upLink = "." %}
{% assign nextTitle = "prop sets, key sets, and index sets" %}
{% assign nextLink = "sets.html" %}
{% include navigate.html %}

