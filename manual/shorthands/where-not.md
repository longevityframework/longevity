---
title: where not to construct your shorthand pools
layout: page
---

TLDR; don't initialize your subdomain within the same object you
declare your shorthand pool.

You've probably noticed that we're pretty pedantic about defining our
shorthand pools within an object named `shorthands`. the reason why we
do this is that we don't want to define the shorthand pool in the same
object that the `Subdomain` is built. If we do, we might run in to
initialization problems. For instance, consider the following example:

```scala
import longevity.subdomain.Shorthand
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.PTypePool
import longevity.subdomain.ptype.RootType

case class Email(email: String)
val emailShorthand = Shorthand[Email, String]
implicit val shorthandPool = ShorthandPool(emailShorthand)

case class User(
  username: String,
  firstName: String,
  lastName: String,
  primaryEmail: Email,
  emails: Set[Email])
extends Root

object User extends RootType[User] {
  object keys {
  }
  object indexes {
  }
}

val subdomain = Subdomain("blogging", PTypePool(User))
```

Let's suppose we access the `User` before the `subdomain` in this
example. Construction of the `User` companion object takes a
`ShorthandPool` as an implicit argument. So in the process of
initializing the `User`, we have to initialize the object containing
the `implicit val shorthandPool`. This initialization entails building
the `EntityTypePool` in the last line of the example. _This_
constructor takes the `User` companion object as argument, which is
still in the process of being initialized! The `EntityTypePool` gets a
`null` constructor argument, and its initializer throws a
`NullPointerException` because of this.

If we define our shorthand pool and our entity type pool in different
objects, then this kind of initialization problem will not occur. Our
preferred approach is to define the subdomain in the package object,
alongside an `object shorthands` in the same package object. Then
where you define your roots, your can provide an import like this:

```scala
import spacetool.domain.shorthands._
```

{% assign prevTitle = "shorthand pools" %}
{% assign prevLink = "shorthand-pools.html" %}
{% assign upTitle = "shorthands" %}
{% assign upLink = "." %}
{% assign nextTitle = "entities" %}
{% assign nextLink = "../entities" %}
{% include navigate.html %}
