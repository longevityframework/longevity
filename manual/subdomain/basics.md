---
title: basic properties
layout: page
---

In the previous example, we saw how to build a `Persistent` with a few
`String` properties. Of course, we can build persistent objects with
other kinds of properties than that. The simplest kinds are ["basic
properties"](http://longevityframework.github.io/longevity/scaladocs/emblem-latest/#emblem.emblematic.basicTypes$),
allowing you to put in properties with a number of simple types:

- `java.lang.String`
- `org.joda.time.DateTime`
- `scala.Boolean`
- `scala.Char`
- `scala.Double`
- `scala.Float`
- `scala.Int`
- `scala.Long`

For example, we might add a few fields to our `User` like so:

```scala
import longevity.subdomain.PType
import longevity.subdomain.PTypePool
import longevity.subdomain.Persistent
import longevity.subdomain.Subdomain
import org.joda.time.DateTime

case class User(
  username: String,
  firstName: String,
  lastName: String,
  dateJoined: DateTime,
  numCats: Int,
  accountSuspended: Boolean = false)
extends Persistent

object User extends PType[User] {
  object props {
  }
  object keys {
  }
}

val subdomain = Subdomain("blogging", PTypePool(User))
```

<div class="blue-side-bar">

We recommend using <a href =
"https://github.com/nscala-time/nscala-time">nscala-time</a> to wrap
your <a href = "http://www.joda.org/joda-time/">Joda-Time</a> dates in
a Scala-friendly wrapper.

</div>

{% assign prevTitle = "persistent types" %}
{% assign prevLink  = "ptypes.html" %}
{% assign upTitle   = "the subdomain" %}
{% assign upLink    = "." %}
{% assign nextTitle = "collections" %}
{% assign nextLink  = "collections.html" %}
{% include navigate.html %}

