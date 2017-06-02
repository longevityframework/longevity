---
title: key values
layout: page
---

Key values are used to uniquely identify a [persistent
object](persistents.html). They can be embedded in persistents in two
different ways. The first way to embed a key value is as a unique
identifier for the persistent in question. For example, we may want to
stipulate that every `User` has a unique `Username`. We can model this
in longevity like so:

```scala
import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@keyVal[DomainModel, User]
case class Username(username: String)

@persistent[DomainModel](keySet = Set(key(props.username)))
case class User(
  username: Username,
  firstName: String,
  lastName: String)
```

As with persistents and components, you need to declare your key values in the same package or a
subpackage of the package where you model is declared.

The `key(props.username)` in the `@persistent` annotation defines the [key](ptype/keys.html) for our
key value type `Username`. We will go into the details of keys in a later section, but we include
them here so that the examples are correct.

The second way we can embed a `KeyVal` in a persistent object is as a
reference to some other persistent object. As an example, let's
suppose that the users in our domain have an optional sponsor. We can
specify the sponsor by providing the sponsor's username, like so:

```scala
import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@keyVal[DomainModel, User]
case class Username(username: String)

@persistent[DomainModel](keySet = Set(key(props.username)))
case class User(
  username: Username,
  firstName: String,
  lastName: String,
  sponsor: Option[Username])
```

This `sponsor` field represents a relationship between two persistent
objects. In UML, we call this kind of relationship an
[aggregation](http://aviadezra.blogspot.com/2009/05/uml-association-aggregation-composition.html). With
aggregations, the life cycles of the entities in question are
independent.

As we can see in the previous example, key values can appear inside
[collections](collections.html). They can also appear within
[components](components.html). Key values can have multiple fields in
them, and they can even embed other key values. But they cannot contain
any collections or [polymorphic objects](../poly).

We can always look up a persistent object by key value using [repository method
`Repo.retrieve`](../repo/retrieve.html), as we will discuss in a later section.

The non-annotation equivalent for the key value looks like this:

```scala
import longevity.model.KVType

case class Username(username: String)

object Username extends KVType[DomainModel, User, Username]
```

The `KVType[DomainModel, User, Username]` contains an implicit `KVEv[DomainModel, User, Username]`,
which gives us compile-time assurance that the key value supplied to `Repo.retrieve` is a valid key
value in the model.

{% assign prevTitle = "components" %}
{% assign prevLink  = "components.html" %}
{% assign upTitle   = "the domain model" %}
{% assign upLink    = "." %}
{% assign nextTitle = "limitations on persistents, components, and key values" %}
{% assign nextLink  = "limitations.html" %}
{% include navigate.html %}
