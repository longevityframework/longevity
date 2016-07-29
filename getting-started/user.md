---
title: building the user aggregate
layout: page
---

The user aggregate has four parts: the `User`, the `UserProfile`, and
two natural keys: the `Username` and the `Email`.  Let's focus on the
`User` first, since that is the heart of the aggregate:

```scala
package simbl.domain

import longevity.subdomain.persistent.Root

case class User(
  username: Username,
  email: Email,
  fullname: String,
  profile: Option[UserProfile])
extends Root {

  def updateProfile(profile: UserProfile): User = copy(profile = Some(profile))

  def deleteProfile: User = copy(profile = None)

}
```

The `User` is a simple case class that extends longevity trait
`Root`. `Root` extends the empty trait `Persistent`, which we use to
mark things we want to persist to the database. We could use
`Persistent` here instead, but we use `Root` because `User` is the
[aggregate root](../manual/ddd-basics/aggregates-and-entities.html).

The `User` case class provides us with the four members we find in the
UML in the [previous section](modelling.html), including the
relationship between `User` and `UserProfile`. There are also a couple
of business methods inside: `updateProfile` and `deleteProfile`.

The `User` companion object provides metadata about the `User`:
information that pertains not to an individual user, but to a
collection of them:

```scala
import longevity.subdomain.ptype.RootType

object User extends RootType[User] {
  object props {
    val username = prop[Username]("username")
    val email = prop[Email]("email")
  }
  object keys {
    val username = key(props.username)
    val email = key(props.email)
  }
}
```

We first specify two properties, `User.props.username` and
`User.props.email`, that we use to refer to members of `User`
instances. Then we define two keys: `User.keys.username` and
`User.keys.email`, that specify that these two member are to be
unique: no two users should have the same username or email.

The `User` companion object extends `RootType`, a subclass of `PType`,
which is a type class for a `Persistent`. Every `PType` defines its
`props`, `keys`, and `indexes`, as you see in this example.

{% assign prevTitle = "modelling our subdomain" %}
{% assign prevLink = "modelling.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="the user profile" %}
{% assign nextLink="user-profile.html" %}
{% include navigate.html %}
