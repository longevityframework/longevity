---
title: embeddables
layout: page
---

Embeddables are a way of nesting case classes inside of your
persistent objects. They never get persisted on their own, but rather
as part of some other `Persistent` type in your subdomain.

For example, let's suppose we want to group the user's `firstName` and
`lastName` fields into a `FullName` case class. We make `FullName`
extend `Embeddable`, and provide an _embeddable type_, or `EType`, via
the companion object:

```scala
import longevity.subdomain.embeddable.EType
import longevity.subdomain.embeddable.Embeddable
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class FullName(
  firstName: String,
  lastName: String)
extends Embeddable

object FullName extends EType[FullName]

case class User(
  username: String,
  fullName: FullName)
extends Root

object User extends RootType[User] {
  object props {
  }
  object keys {
  }
  object indexes {
  }
}
```

Now when we create our subdomain, we need to mention the embeddable
type in the _embeddable type pool_, or `ETypePool`:

```scala
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.ptype.PTypePool

val subdomain = Subdomain("blogging", PTypePool(User), ETypePool(FullName))
```

You can put embeddables in embeddables, and embeddables into
[supported collection types](../collections.html) `Option`, `Set` and
`List`, collections into embeddables. For example:

```scala
import longevity.subdomain.embeddable.EType
import longevity.subdomain.embeddable.Embeddable
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class Email(email: String) extends Embeddable

object Email extends EType[Email]

case class EmailPreferences(
  primaryEmail: Email,
  emails: Set[Email])
extends Embeddable

object EmailPreferences extends EType[EmailPreferences]

case class Address(
  street: String,
  city: String)
extends Embeddable

object Address extends EType[Address]

case class User(
  username: String,
  emails: EmailPreferences,
  addresses: Set[Address])
extends Root

object User extends RootType[User] {
  object props {
  }
  object keys {
  }
  object indexes {
  }
}

import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.ptype.PTypePool

val subdomain = Subdomain("blogging", PTypePool(User), ETypePool(Address, Email, EmailPreferences))
```

Typically, embeddables are entities or value objects when doing
traditional DDD modelling. We provide `Embeddable` sub-traits `Entity`
and `ValueObject`, and you can use whatever terminology suits you.
The more generic `Embeddable` is probably a better term for things
that are embedded in `Persistents`, `Events`, or `ViewItems`. So far
as longevity is concerned, you can use `Embeddable`, `Entity`, and
`ValueObject` interchangeably.

{% assign prevTitle = "collections" %}
{% assign prevLink = "../collections.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "entities" %}
{% assign nextLink = "entities.html" %}
{% include navigate.html %}

