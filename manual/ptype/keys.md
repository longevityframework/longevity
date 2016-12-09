---
title: keys
layout: page
---

Keys are composed of a single property whose type is [key
value](../subdomain/key-values.html) for the persistent object. To declare a
key, we use `PType` method `key`, and add it into our `PType.keySet`,
like so:

```scala
import longevity.subdomain.annotations.keyVal
import longevity.subdomain.annotations.persistent

@keyVal[User]
case class Username(username: String)

@persistent(keySet = Set(
  key(User.props.username)))
case class User(
  username: Username,
  firstName: String,
  lastName: String)
```

Because the `key(User.props.username)` definition will wind up inside
our `User` companion object, we can say `key(props.username)`
instead, leaving off the `User.` prefix.

The non-annotation equivalent of the above is like so:

```scala
import longevity.subdomain.KeyVal
import longevity.subdomain.PType

case class Username(username: String) extends KeyVal[User]

case class User(
  username: Username,
  firstName: String,
  lastName: String)

object User extends PType[User] {
  object props {
    val username = prop[Username]("username")
    // ...
  }
  lazy val keySet = Set(key(props.username))
}
```

Keys play two important roles in your domain model. First, they
indicate that a key value should uniquely identify a persistent
object. So in the above example, no two users can have the same
username. Second, they indicate that looking up a persistent object by
key value should be fast.

We can declare multiple keys, and composite keys, just as
easily. Here, for instance, we add an ill-advised composite key on a
`firstName`/`lastName` combination:

```scala
import longevity.subdomain.annotations.keyVal
import longevity.subdomain.annotations.persistent

@keyVal[User]
case class Username(username: String)

@keyVal[User]
case class FullName(last: String, first: String)

@persistent(keySet = Set(
  key(props.username),
  key(props.fullName)))
case class User(
  username: Username,
  fullName: FullName)
```

Here, no two users can have the same first and last names.

We use keys to retrieve individual persistent objects from the
persistence layer, as we will see in the [section on
`Repo.retrieve`](../repo/retrieve.html). You are most likely
going to want to define at least one key per persistent type, or you
will only be able to retrieve collections of persistent objects [by
query](../query/retrieve-by.html). It is possible that you have a persistent
type - perhaps representing an entry in a log file - for which there
are no natural keys. You may be satisfied to confine yourself to
looking up collections of these objects via range searches.

{% assign prevTitle = "properties" %}
{% assign prevLink = "properties.html" %}
{% assign upTitle = "the persistent type" %}
{% assign upLink = "." %}
{% assign nextTitle = "partition keys" %}
{% assign nextLink = "partition-keys.html" %}
{% include navigate.html %}

