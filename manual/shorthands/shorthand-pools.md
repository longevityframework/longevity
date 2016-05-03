---
title: shorthand pools
layout: page
---

You can think of a `ShorthandPool` as an immutable set of
`Shorthands`. You can create an empty one like this:

```scala
val pool = ShorthandPool()
```

Or like this:

```scala
val pool = ShorthandPool.empty
```

You can put a number of shorthands in a pool like so:

```scala
val pool = ShorthandPool(emailShorthand, markdownShorthand, uriShorthand)
```

And you can build them with the `+` operator as well:

```scala
val pool = ShorthandPool() + emailShorthand + markdownShorthand + uriShorthand
```

Shorthand pools are needed to construct your `RootTypes`, as
well as your `Subdomains`. We normally provide the pool implicitly,
like so:

```scala
object shorthands {
  implicit val shorthandPool = ShorthandPool(emailShorthand, markdownShorthand, uriShorthand)
}
import shorthands._
object User extends RootEntityType[User]
val subdomain = Subdomain("blogging", PTypePool(User))
```

If you want to be explicit about it, you can do it like so:

```scala
import emblem.imports.typeKey
object shorthands {
  val shorthandPool = ShorthandPool(emailShorthand, markdownShorthand, uriShorthand)
}
object User extends RootEntityType()(typeKey[User], shorthands.shorthandPool) {
  object keys {
  }
  object indexes {
  }
}
val subdomain = Subdomain("blogging", PTypePool(User))(shorthands.shorthandPool)
```

If you don't supply a `ShorthandPool`, an empty one will be provided
for you. If you make use of a shorthand that's not in the pool, you
won't find out until some time after your `Subdomain` is
constructed. [We plan to remedy
things](https://www.pivotaltracker.com/story/show/99755864) so that
these kinds of errors get reported on construction of your `Subdomain`
or your `RootType`. In the meantime, longevity provides [test
support](../testing.html) to exercise persistence operations on your
`Subdomain`, and this kind of error will be exposed by these tests.

{% assign prevTitle = "shorthands" %}
{% assign prevLink = "." %}
{% assign upTitle = "shorthands" %}
{% assign upLink = "." %}
{% assign nextTitle = "where not to construct your shorthand pools" %}
{% assign nextLink = "where-not.html" %}
{% include navigate.html %}

