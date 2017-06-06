---
title: collections
layout: page
---

Longevity supports the following "collection" types within your
domain objects:

- `scala.Option`
- `scala.collection.immutable.Set`
- `scala.collection.immutable.List`

For example, we can add an optional title property to our user, to
hold values like "Mr.", "Mrs.", "Sir", and "Brother". And we can allow
the user to have multiple emails:

```scala
import longevity.model.annotations.persistent

@persistent[DomainModel]
case class User(
  username: String,
  title: Option[String],
  firstName: String,
  lastName: String,
  emails: Set[String])
```

It's on our TODO list to [handle a wider variety of collection
types](https://www.pivotaltracker.com/story/show/88571474), including `Maps`. But this basic set of
collections should satisfy your needs. If you are itching to use another collection type in your
domain model, please [let us know](http://longevityframework.org/discussions.html)! Please note that
we will only ever support immutable collections. It is important that the persistent objects are
immutable, so that longevity can [keep track of any changes](../repo/persistent-state.html).

{% assign prevTitle = "basic values" %}
{% assign prevLink  = "basics.html" %}
{% assign upTitle   = "the domain model" %}
{% assign upLink    = "." %}
{% assign nextTitle = "components" %}
{% assign nextLink  = "components.html" %}
{% include navigate.html %}
