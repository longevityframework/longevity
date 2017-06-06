---
title: basic values
layout: page
---

In the previous examples, we saw how to build a persistent object with a few `String` members. Of
course, we can build persistent objects with other kinds of members than that. The simplest kinds
are _basic values_, allowing you to put in members with a number of simple types:

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
import longevity.model.annotations.persistent
import org.joda.time.DateTime

@persistent[DomainModel]
case class User(
  username: String,
  firstName: String,
  lastName: String,
  dateJoined: DateTime,
  numCats: Int,
  accountSuspended: Boolean = false)
```

<div class="blue-side-bar">

We recommend using <a href =
"https://github.com/nscala-time/nscala-time">nscala-time</a> to wrap
your <a href = "http://www.joda.org/joda-time/">Joda-Time</a> dates in
a Scala-friendly wrapper.

</div>

{% assign prevTitle = "persistent objects" %}
{% assign prevLink  = "persistents.html" %}
{% assign upTitle   = "the domain model" %}
{% assign upLink    = "." %}
{% assign nextTitle = "collections" %}
{% assign nextLink  = "collections.html" %}
{% include navigate.html %}

