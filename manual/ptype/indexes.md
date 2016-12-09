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
import longevity.model.annotations.keyVal
import longevity.model.annotations.component
import longevity.model.annotations.persistent

@keyVal[User]
case class Username(username: String)

@component
case class FullName(last: String, first: String)

@persistent(
  keySet = Set(key(props.username)),
  indexSet = Set(index(props.fullName.last, props.fullName.first)))
case class User(
  username: Username,
  fullName: FullName)
```

The index above will assure fast performance for queries that filter
on `lastName`, as well as for queries where `lastName` is fixed and
`firstName` is filtered. It will not assure performance for a search
on `firstName` alone.

The equivalent non-annotation version is like so:

```scala
import longevity.model.KeyVal
import longevity.model.CType
import longevity.model.PType

case class Username(username: String) extends KeyVal[User]

case class FullName(last: String, first: String)

object FullName extends CType[FullName]

case class User(
  username: Username,
  fullName: FullName)

object User extends PType[User] {
  object props {
    val username = prop[Username]("username")
    val lastName = prop[String]("fullName.last")
    val firstName = prop[String]("fullName.first")
  }
  lazy val keySet = Set(key(props.username))
  override lazy val indexSet = Set(index(props.lastName, props.firstName))
}
```

Note that `indexSet` is defined in `PType` to be the empty set, so if
you want to add indexes, you have to override `indexSet`. While we
expect nearly every persistent type to contain keys, we expect many
users will have no need for indexes. In NoSQL, indexes are vaguely
frowned upon. A preferred approach would be to maintain a [secondary
view table](http://martinfowler.com/bliki/CQRS.html) that will bypass
the need for an index.

Indexes are used by `Repo.retrieveByQuery`, which is described in a [later
section](../query/retrieve-by.html).

{% assign prevTitle = "partition keys" %}
{% assign prevLink = "partition-keys.html" %}
{% assign upTitle = "the persistent type" %}
{% assign upLink = "." %}
{% assign nextTitle = "property sets" %}
{% assign nextLink = "sets.html" %}
{% include navigate.html %}

