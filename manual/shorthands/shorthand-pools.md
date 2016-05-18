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
val pool = ShorthandPool(Email, Markdown, Uri)
```

And you can build them with the `+` operator as well:

```scala
val pool = ShorthandPool() + Email + Markdown + Uri
```

You provide your shorthand pool when constructing your subdomain:

```scala
val subdomain = Subdomain(
  "blogging",
  PTypePool(Blog, BlogPost, User),
  shorthandPool = ShorthandPool(Email, Markdown, Uri))
```

If you don't supply a `ShorthandPool`, an empty one will be provided
for you. If you make use of a shorthand that's not in the pool, you
won't find out until some time after your `Subdomain` is
constructed. [We plan to remedy
things](https://www.pivotaltracker.com/story/show/99755864) so that
these kinds of errors get reported on construction of your `Subdomain`
or your `PType`. In the meantime, longevity provides [test
support](../testing.html) to exercise persistence operations on your
`Subdomain`, and this kind of error will be exposed by these tests.

{% assign prevTitle = "shorthands" %}
{% assign prevLink = "." %}
{% assign upTitle = "shorthands" %}
{% assign upLink = "." %}
{% assign nextTitle = "entities" %}
{% assign nextLink = "../entities" %}
{% include navigate.html %}

