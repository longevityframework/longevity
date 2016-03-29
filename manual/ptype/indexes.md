---
title: indexes
layout: page
---

An index in longevity is a requirement that certain forms of queries
on our aggregates should perform with low latency. For instance, if
there is an expectation that queries on `lastName` / `firstName`
should perform quickly, then we need to define an index, like so:

```scala
import longevity.subdomain.EntityTypePool
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class User(
  username: String,
  firstName: String,
  lastName: String)
extends Root

object User extends RootType[User] {
  object props {
    val username = prop[String]("username")
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

val subdomain = Subdomain("blogging", EntityTypePool(User))
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
{% assign nextTitle = "key sets and index sets" %}
{% assign nextLink = "key-sets-and-index-sets.html" %}
{% include navigate.html %}

