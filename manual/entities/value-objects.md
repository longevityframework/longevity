---
title: entities and value objects
layout: page
---

Traditionally in Domain Driven Design, there are three fundamental
building blocks: the entity, the aggregate, and the [value
object](https://lostechies.com/joeocampo/2007/04/23/a-discussion-on-domain-driven-design-value-objects/).
Value objects are like entities, but they are typically represented as
immutable objects. In longevity, the entity objects are immutable as
well, so this doesn't differentiate them much. We also choose to model
things as value objects when we don't care about identity. But it's
not exactly clear what affect this has on an implementation. In a
relational database, we would be inclined to give an entity a primary
key, and not do so with a value object. But with a document database,
entities and value objects both attain their identity from their
position within the aggregate, or document.

Consequently, longevity does not distinguish between entities and
value objects. But we happily support the terminology by providing
`ValueObject` and `ValueType` as synonyms for `Entity` and
`EntityType`, respectively. For example, we can write the following:

```scala
import longevity.subdomain.EntityTypePool
import longevity.subdomain.Subdomain
import longevity.subdomain.ValueObject
import longevity.subdomain.ValueType
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class Address(
  street: String,
  city: String,
  state: StateCode,
  zip: ZipCode)
extends ValueObject

object Address extends ValueType[Address]

case class User(
  username: String,
  email: Email,
  address: Address)
extends Root

object User extends RootType[User] {
  object keys {
  }
  object indexes {
  }
}

val subdomain = Subdomain("blogging", EntityTypePool(User, Address))
```

And it is entirely equivalent to this:

```scala
import longevity.subdomain.Entity
import longevity.subdomain.EntityType
import longevity.subdomain.EntityTypePool
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class Address(
  street: String,
  city: String,
  state: StateCode,
  zip: ZipCode)
extends Entity

object Address extends EntityType[Address]

case class User(
  username: String,
  email: Email,
  address: Address)
extends Root

object User extends RootType[User] {
  object keys {
  }
  object indexes {
  }
}

val subdomain = Subdomain("blogging", EntityTypePool(User, Address))
```

For a more extended discussion on value objects in an immutable
context, please see this blog post on [entities, value objects, and
identity](http://scabl.blogspot.com/2015/05/aeddd-13.html).

{% assign prevTitle = "entities" %}
{% assign prevLink = "." %}
{% assign upTitle = "entities" %}
{% assign upLink = "." %}
{% assign nextTitle = "limitations on entities and shorthands" %}
{% assign nextLink = "limitations.html" %}
{% include navigate.html %}

