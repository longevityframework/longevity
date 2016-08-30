---
title: value objects
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
`ValueObject` and `ValueType` as synonyms for `Embeddable` and
`EType`, respectively.

Typically, we are more comfortable calling things data objects when
they are easily viewed as data objects, such as an address. They are
also useful to single-valued case classes that we use instead of raw
values for better typing, such as the `ZipCode` and `Email` in the
example below:

```scala
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.PTypePool
import longevity.subdomain.ptype.RootType

case class Email(email: String) extends ValueObject
case class StateCode(stateCode: String) extends ValueObject
case class ZipCode(zipCode: String) extends ValueObject

case class Address(
  street: String,
  city: String,
  state: StateCode,
  zip: ZipCode)
extends ValueObject

case class User(
  username: String,
  email: Email,
  address: Address)
extends Root

object User extends RootType[User] {
  object props {
  }
  object keys {
  }
}

val subdomain = Subdomain(
  "blogging",
  PTypePool(User),
  ETypePool(ValueType[Email], ValueType[StateCode], ValueType[ZipCode], ValueType[Address]))
```

For a more extended discussion on value objects in an immutable
context, please see this blog post on [entities, value objects, and
identity](http://scabl.blogspot.com/2015/05/aeddd-13.html).

{% assign prevTitle = "entities" %}
{% assign prevLink = "entities.html" %}
{% assign upTitle = "embeddables" %}
{% assign upLink = "." %}
{% assign nextTitle = "key values" %}
{% assign nextLink = "../key-values.html" %}
{% include navigate.html %}

