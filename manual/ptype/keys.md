---
title: keys
layout: page
---

Keys are composed of a single property whose type is [key
value](../key-values.html) for the `Persistent`. We define them in our
`PType` like so:

```scala
import longevity.subdomain.KeyVal
import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class Username(username: String) extends KeyVal[User, Username]

case class User(
  username: Username,
  firstName: String,
  lastName: String)
extends Persistent

object User extends PType[User] {
  object props {
    val username = prop[Username]("username")
  }
  object keys {
    val username = key(props.username)  
  }
}
```

The key value uniquely identifies a persistent object. So in
the above example, no two users can have the same username.

We can declare multiple keys, and composite keys, just as
easily. Here, for instance, we add an ill-advised composite key on a
`firstName`/`lastName` combination:

```scala
import longevity.subdomain.KeyVal
import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class Username(username: String)
extends KeyVal[User, Username]

case class FullName(first: String, last: String)
extends KeyVal[User, FullName]

case class User(
  username: Username,
  fullName: FullName)
extends Persistent

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

It's worth reiterating that the keys that we define in our domain
model are not database keys, but design constraints that live within
the domain model itself. That being said, they do map to database keys
in predictable ways. See the chapter on [translating persistents to
the database](../translation) for more information.

{% assign prevTitle = "properties" %}
{% assign prevLink = "properties.html" %}
{% assign upTitle = "the persistent type" %}
{% assign upLink = "." %}
{% assign nextTitle = "indexes" %}
{% assign nextLink = "indexes.html" %}
{% include navigate.html %}

