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

@persistent[DomainModel]
case class User(
  username: Username,
  firstName: String,
  lastName: String)

object User {
  implicit val usernameKey = key(props.username)
}
```

As with persistents and components, you need to declare your key values in the same package or a
subpackage of the package where you model is declared.

The `implicit val usernameKey` defines the [key](../ptype/keys.html) for our key value type `Username`.
As you can see, this makes use of the `object props` embedded in `object User` that we saw towards
the bottom of the [chapter on persistents](persistents.html). Without the `@persistent` annotation
on the case class, this wouldn't compile at all. But if we recall how this annotation expands, we
see if makes sense. The `props.username` is put in by the `@mprops` annotation, and the method `key`
comes from the fact that `object User` has become a `PType` ([scaladoc here](../../api/longevity/model/PType.html#key[V](keyValProp:longevity.model.ptype.Prop[P,V])(implicitevidence$4:longevity.model.KVEv[M,P,V]):longevity.model.ptype.Key[M,P,V])).

We'll talk more about keys in a [later section](../ptype/keys.html).

The second way we can embed a `KeyVal` in a persistent object is as a reference to some other
persistent object. As an example, let's suppose that the users in our domain have an optional
sponsor. We can specify the sponsor by providing the sponsor's username, like so:

```scala
import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@keyVal[DomainModel, User]
case class Username(username: String)

@persistent[DomainModel]
case class User(
  username: Username,
  firstName: String,
  lastName: String,
  sponsor: Option[Username])

object User {
  implicit val usernameKey = key(props.username)
}
```

This `sponsor` field represents a relationship between two persistent objects. In UML, we call this
kind of relationship an
[aggregation](http://aviadezra.blogspot.com/2009/05/uml-association-aggregation-composition.html).
With aggregations, the life cycles of the entities in question are independent.

As we can see in the previous example, key values can appear inside [collections](collections.html).
They can also have multiple fields, and appear within [components](components.html). But they
currently cannot contain any collections or [polymorphic objects](../poly).

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
