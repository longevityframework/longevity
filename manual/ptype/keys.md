---
title: keys
layout: page
---

Keys are composed of a single property whose type is [key
value](../key-values.html) for the persistent object. We define them
in our `PType` like so:

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
  }
  object keys {
    val username = key(props.username)  
  }
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
import longevity.subdomain.KeyVal
import longevity.subdomain.PType

case class Username(username: String) extends KeyVal[User]

case class FullName(last: String, first: String) extends KeyVal[User]

case class User(
  username: Username,
  fullName: FullName)

object User extends PType[User] {
  object props {
    val username = prop[Username]("username")
    val fullName = prop[FullName]("fullName")
  }
  object keys {
    val username = key(props.username)
    val fullName = key(props.fullName)
  }
}
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

