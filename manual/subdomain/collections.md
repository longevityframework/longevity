---
title: collections
layout: page
---

Longevity supports the following "collection" types within your
subdomain entities:

- `scala.Option`
- `scala.collection.immutable.Set`
- `scala.collection.immutable.List`

For example, we can add an optional title property to our user, to
hold values like "Mr.", "Mrs.", "Sir", and "Brother". And we can allow
the user to have multiple emails:

```scala
import longevity.subdomain.EntityTypePool
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class User(
  username: String,
  title: Option[String],
  firstName: String,
  lastName: String,
  emails: Set[String])
extends Root

object User extends RootType[User]

val subdomain = Subdomain("blogging", EntityTypePool(User))
```

It's on our TODO list to [handle a wider variety of collection
types](https://www.pivotaltracker.com/story/show/88571474), including
`Maps`. But this basic set of collections should satisfy your
needs. If you are itching to use another collection type in your
subdomain, please [let us
know](http://longevityframework.github.io/longevity/discussions.html)!
But please note that we will only ever support immutable
collections. It is important for the aggregates to be entirely
immutable, so that longevity can [keep track of any
changes](../context/persistent-state.html).

{% assign prevTitle = "basic properties" %}
{% assign prevLink = "basics.html" %}
{% assign upTitle = "building your subdomain" %}
{% assign upLink = "." %}
{% assign nextTitle = "shorthands" %}
{% assign nextLink = "../shorthands" %}
{% include navigate.html %}
