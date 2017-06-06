---
title: keys
layout: page
---

Keys are composed of a single property whose type is [key value](../model/key-values.html) for the
persistent object. To declare a key, we use `PType` method `key` like so:

```scala
import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@keyVal[User]
case class Username(username: String)

@persistent
case class User(
  username: Username,
  firstName: String,
  lastName: String)

object User {
  implicit val usernameKey = key(props.username)
}
```

We make the key implicit because it is used as an implicit parameter to the `Repo.retrieve` and
`Repo.retrieveOne` methods, as we will [see later](../repo/retrieve.html).

Keys play two important roles in your domain model. First, they indicate that a key value should
uniquely identify a persistent object. So in the above example, no two users can have the same
username. Second, they indicate that looking up a persistent object by key value should be fast.

We can declare multiple keys, and composite keys, just as
easily. Here, for instance, we add an ill-advised composite key on a
`firstName`/`lastName` combination:

```scala

import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@keyVal[DomainModel, User]
case class Username(username: String)

@keyVal[DomainModel, User]
case class FullName(last: String, first: String)

@persistent[DomainModel]
case class User(
  username: Username,
  fullName: FullName)

object User {
  implicit val usernameKey = key(props.username)
  implicit val fullNameKey = key(props.fullName)
}
```

Here, no two users can have the same first and last names.

We use keys to retrieve individual persistent objects from the persistence layer, as we will see in
the [section on `Repo.retrieve`](../repo/retrieve.html). You are most likely going to want to define
at least one key per persistent type, or you will only be able to retrieve collections of persistent
objects [by query](../query/retrieve-by.html). It is possible that you have a persistent type -
perhaps representing an entry in a log file - for which there are no natural keys. You may be
satisfied to confine yourself to looking up collections of these objects via range searches.

{% assign prevTitle = "properties" %}
{% assign prevLink = "properties.html" %}
{% assign upTitle = "the persistent type" %}
{% assign upLink = "." %}
{% assign nextTitle = "primary keys" %}
{% assign nextLink = "primary-keys.html" %}
{% include navigate.html %}

