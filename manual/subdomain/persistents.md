---
title: persistent objects
layout: page
---

As longevity is primarily a persistence framework, the common currency
of the longevity API is the _persistent object_. Persistent objects
are part of your subdomain, and they are also the _persistence unit_ -
something that you can create, retrieve, update or delete with a
longevity [repository](../repo).

Persistent objects are Scala case classes that meet some basic
criteria. Those criteria are laid out in the chapters that follow.

Here's a simple example:

```scala
case class User(
  username: String,
  firstName: String,
  lastName: String)
```

To tell longevity that this is a persistent object, we simply mark the
class with a `persistent` annotation. At a minimum, we need to provide
a `keySet` to the `persistent` annotation. For now, we can provide an
empty set of keys, like so:

```scala
import longevity.subdomain.annotations.persistent

@persistent(keySet = emptyKeySet)
case class User(
  username: String,
  firstName: String,
  lastName: String)
```

Here is how you would do the same thing without the annotation:

```scala
import longevity.subdomain.PType

case class User(
  username: String,
  firstName: String,
  lastName: String)

object User extends PType[User] {
  object props {
    // ...
  }
  val keySet = emptyKeySet
}
```

{% assign prevTitle = "the subdomain" %}
{% assign prevLink  = "." %}
{% assign upTitle   = "the subdomain" %}
{% assign upLink    = "." %}
{% assign nextTitle = "basic values" %}
{% assign nextLink  = "basics.html" %}
{% include navigate.html %}
