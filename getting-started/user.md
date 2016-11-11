---
title: building the user
layout: page
---

The user has four parts: the `User`, the `UserProfile`, and two
natural keys: the `Username` and the `Email`.  Let's focus on the
`User` first:

```scala
package simbl.domain

import longevity.subdomain.Persistent

case class User(
  username: Username,
  email: Email,
  fullname: String,
  profile: Option[UserProfile])
extends Persistent {

  def updateProfile(profile: UserProfile): User = copy(profile = Some(profile))

  def deleteProfile: User = copy(profile = None)

}
```

The `User` is a simple case class that extends empty longevity trait
`Persistent`, which we use to mark things we want to persist to the
database.

The `User` case class provides us with the four members we find in the
UML in the [previous section](modelling.html), including the
relationship between `User` and `UserProfile`. There are also a couple
of business methods inside: `updateProfile` and `deleteProfile`.

The `User` companion object provides metadata about the `User`:
information that pertains not to an individual user, but to a
collection of them:

```scala
import longevity.subdomain.PType

object User extends PType[User] {
  object props {
    val username = prop[Username]("username")
    val email = prop[Email]("email")
  }
  object keys {
    val username = partitionKey(props.username)
    val email = key(props.email)
  }
}
```

We first specify two properties, `User.props.username` and
`User.props.email`, that we use to refer to members of `User`
instances. Then we define two keys: `User.keys.username` and
`User.keys.email`, that specify that these two member are to be
unique: no two users should have the same username or email.

You can have as many keys as you like, but only one of the keys - in
our case, `username` - can be a partition key. Partition keys perform
better than other keys when you are using a distributed database,
since the database can determine the node that holds the data by
examining the key.

The `User` companion object extends `PType`, which is a type class for
a `Persistent`. Every `PType` defines its `props`, `keys`, and
`indexes`, as you see in this example.

{% assign prevTitle = "modelling our subdomain" %}
{% assign prevLink = "modelling.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="the user profile" %}
{% assign nextLink="user-profile.html" %}
{% include navigate.html %}
