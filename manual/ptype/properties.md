---
title: properties
layout: page
---

In our `PType`, when we talk about the fields of the `Persistent`
type, we talk about properties, or `Props`. Properties map to
underlying members within the [persistent object](../persistent), at
any depth. They follow a path from the root of the persistent
object, and take on the type of that member in the persistent. We
typically define them in a singleton object `props` inside the
`PType`. Here's an example defining a couple of properties:

```scala
import longevity.subdomain.embeddable.Entity
import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)
extends Entity

case class User(
  username: String,
  email: Email,
  profile: UserProfile)
extends Persistent

object User extends PType[User] {
  object props {

    // fully typed:
    val profileDescription: longevity.subdomain.ptype.Prop[User, Markdown] =
      prop[Markdown]("profile.description")

    // brief:
    val usernameProp = prop[String]("username")
  }
  object keys {
  }
}
```

You need to specify the type of the property yourself, and longevity
will check that the type is correct when the `Subdomain` is created.

In principle, properties could map through any path from the
persistent object, and have a wide variety of types. In practice, we
currently support property types that can boiled down to a distinct
sequence of [basic values](../basics.html). You cannot currently use
collection types such as `Option`, `Set`, and `List` anywhere along
the property path, and the property path cannot terminate with a
[polymorphic entity](../poly).

Properties are used to build [keys](keys.html),
[indexes](indexes.html), and [queries](../repo/query.html). We're
interested in looking at a more fluent API for creating properties,
possibly using
[dynamics](http://www.scala-lang.org/api/current/index.html#scala.Dynamic)
and/or
[macros](http://docs.scala-lang.org/overviews/macros/overview.html).

{% assign prevTitle = "the persistent type" %}
{% assign prevLink = "." %}
{% assign upTitle = "the persistent type" %}
{% assign upLink = "." %}
{% assign nextTitle = "keys" %}
{% assign nextLink = "keys.html" %}
{% include navigate.html %}

