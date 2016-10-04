---
title: embeddables
layout: page
---

Embeddables are a way of nesting case classes inside of your
persistent objects. They never get persisted on their own, but rather
as part of some other `Persistent` type in your subdomain.

For example, let's suppose we want to group the user's `firstName` and
`lastName` fields into a `FullName` case class. We make `FullName`
extend `Embeddable`:

```scala
import longevity.subdomain.Embeddable
import longevity.subdomain.PType
import longevity.subdomain.Persistent

case class FullName(
  firstName: String,
  lastName: String)
extends Embeddable

case class User(
  username: String,
  fullName: FullName)
extends Persistent

object User extends PType[User] {
  object props {
  }
  object keys {
  }
}
```

Now when we create our subdomain, we need to provide an _embeddable
type_, or `EType`, for `FullName`, and include it in the _embeddable
type pool_, or `ETypePool`:

```scala
import longevity.subdomain.EType
import longevity.subdomain.ETypePool
import longevity.subdomain.PTypePool
import longevity.subdomain.Subdomain

val subdomain = Subdomain("blogging", PTypePool(User), ETypePool(EType[FullName]))
```

If you prefer, you can create your `EType` by extending the `FullName`
companion object, like so:

```scala
object FullName extends EType[FullName]

val subdomain = Subdomain("blogging", PTypePool(User), ETypePool(FullName))
```

You can put embeddables in embeddables, and embeddables into
[supported collection types](../collections.html) `Option`, `Set` and
`List`, collections into embeddables. For example:

```scala
import longevity.subdomain.Embeddable
import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class Email(email: String) extends Embeddable

case class EmailPreferences(
  primaryEmail: Email,
  emails: Set[Email])
extends Embeddable

case class Address(
  street: String,
  city: String)
extends Embeddable

case class User(
  username: String,
  emails: EmailPreferences,
  addresses: Set[Address])
extends Persistent

object User extends PType[User] {
  object props {
  }
  object keys {
  }
}

import longevity.subdomain.Subdomain
import longevity.subdomain.EType
import longevity.subdomain.ETypePool
import longevity.subdomain.PTypePool

val subdomain = Subdomain(
  "blogging",
  PTypePool(User),
  ETypePool(EType[Address], EType[Email], EType[EmailPreferences]))
```

Having to list all the `PTypes` and `ETypes` to construct the
subdomain is a bit of unfortunate boilerplate. We plan to address this
soon by [supporting classpath
scanning](https://www.pivotaltracker.com/story/show/127406543).

{% assign prevTitle = "collections" %}
{% assign prevLink = "../collections.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "key values" %}
{% assign nextLink = "../key-values.html" %}
{% include navigate.html %}

