---
title: persistent components
layout: page
---

Persistent components are a way of nesting case classes inside of your
persistent objects. They never get persisted on their own, but rather
as part of some other persistent type in your subdomain.

For example, let's suppose we want to group the user's `firstName` and
`lastName` fields into a `FullName` case class:

```scala
import longevity.subdomain.PType

case class FullName(
  firstName: String,
  lastName: String)

case class User(
  username: String,
  fullName: FullName)

object User extends PType[User] {
  object props {
  }
  object keys {
  }
}
```

Now when we create our subdomain, we need to provide a _component
type_, or `CType`, for `FullName`, and include it in the _component
type pool_, or `CTypePool`:

```scala
import longevity.subdomain.CType
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool
import longevity.subdomain.Subdomain

val subdomain = Subdomain("blogging", PTypePool(User), CTypePool(CType[FullName]))
```

If you prefer, you can create your `CType` by extending the `FullName`
companion object, like so:

```scala
object FullName extends CType[FullName]

val subdomain = Subdomain("blogging", PTypePool(User), CTypePool(FullName))
```

You can put components in components, and components in
[collections](../collections.html), and collections in components. For
example:

```scala
import longevity.subdomain.PType

case class Email(email: String)

case class EmailPreferences(
  primaryEmail: Email,
  emails: Set[Email])

case class Address(
  street: String,
  city: String)

case class User(
  username: String,
  emails: EmailPreferences,
  addresses: Set[Address])

object User extends PType[User] {
  object props {
  }
  object keys {
  }
}

import longevity.subdomain.Subdomain
import longevity.subdomain.CType
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool

val subdomain = Subdomain(
  "blogging",
  PTypePool(User),
  CTypePool(CType[Address], CType[Email], CType[EmailPreferences]))
```

Having to list all the `PTypes` and `CTypes` to construct the
subdomain is a bit of unfortunate boilerplate. We plan to address this
soon by [supporting classpath
scanning](https://www.pivotaltracker.com/story/show/127406543).

{% assign prevTitle = "collections" %}
{% assign prevLink  = "collections.html" %}
{% assign upTitle   = "the subdomain" %}
{% assign upLink    = "." %}
{% assign nextTitle = "key values" %}
{% assign nextLink  = "key-values.html" %}
{% include navigate.html %}

