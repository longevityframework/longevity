---
title: properties
layout: page
---

In our `PType`, when we talk about the fields of our persistent
object, we talk about properties, or `Props`. Properties map to
underlying members within the [persistent object](../persistent), at
any depth. They follow a path from the root of the persistent object,
and take on the type of that member in the persistent. We typically
define them in a singleton object `props` inside the `PType`. Here's
an example defining a couple of properties:

```scala
import longevity.subdomain.PType

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)

case class User(
  username: String,
  email: Email,
  profile: UserProfile)

object User extends PType[User] {
  object props {

    // fully typed:
    val profileDescription: longevity.subdomain.ptype.Prop[User, Markdown] =
      prop[Markdown]("profile.description")

    // brief:
    val username = prop[String]("username")
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
[polymorphic object](../poly).

We're planning on building these properties for you using
[macros](http://docs.scala-lang.org/overviews/macros/overview.html). This
will remove the hassle of declaring properties by hand, and get rid of
the runtime type check.

{% assign prevTitle = "the persistent type" %}
{% assign prevLink = "." %}
{% assign upTitle = "the persistent type" %}
{% assign upLink = "." %}
{% assign nextTitle = "keys" %}
{% assign nextLink = "keys.html" %}
{% include navigate.html %}

