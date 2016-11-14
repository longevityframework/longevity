---
title: key values
layout: page
---

Key values are used to uniquely identify a [persistent
object](persistent). They can be embedded in persistents in two
different ways. The first way to embed a key value is as a unique
identifier for the persistent in question. For example, our subdomain
may stipulate that every `User` has a unique `Username`. We can model
this in longevity like so:

```scala
import longevity.subdomain.KeyVal

case class Username(username: String) extends KeyVal[User, Username]

import longevity.subdomain.Persistent

case class User(
  username: Username,
  firstName: String,
  lastName: String)
extends Persistent

import longevity.subdomain.PType

object User extends PType[User] {
  object props {
    val username = prop[Username]("username")
  }
  object keys {
    val username = key(props.username)
  }
}
```

The `User.keys.username` field defines the [key](ptype/keys.html) for
our key value type `Username`. We will go into the details of keys in
a later section, but we include them here so that the examples are
correct.

The second way we can embed a `KeyVal` in a persistent object is as a
reference to some other persistent object. As an example, let's
suppose that the users in our domain have an optional sponsor. We can
specify the sponsor by providing the sponsor's username, like so:

```scala
import longevity.subdomain.KeyVal

case class Username(username: String) extends KeyVal[User, Username]

import longevity.subdomain.Persistent

case class User(
  username: Username,
  firstName: String,
  lastName: String,
  sponsor: Option[Username])
extends Persistent
```

This `sponsor` field represents a relationship between two persistent
objects. In UML, we call this kind of relationship an
[aggregation](http://aviadezra.blogspot.com/2009/05/uml-association-aggregation-composition.html). With
aggregations, the life cycles of the entities in question are
independent.

As we can see in the previous example, `KeyVals` can appear inside
[collections](collections.html). They can also appear within
[embeddables](embeddable). `KeyVals` can have multiple fields in
them, and they can even embed other `KeyVals`. But they cannot contain
any collections or [polymorphic objects](poly).

We can always look up a persistent object by `KeyVal` using
[repository method `Repo.retrieve`](repo/retrieve.html), as we
will discuss in a later section.

{% assign prevTitle = "embeddables" %}
{% assign prevLink  = "embeddables.html" %}
{% assign upTitle   = "the subdomain" %}
{% assign upLink    = "." %}
{% assign nextTitle = "limitations on persistents, embeddables, and key values" %}
{% assign nextLink  = "limitations.html" %}
{% include navigate.html %}
