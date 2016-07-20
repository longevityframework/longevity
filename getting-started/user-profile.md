---
title: the user profile
layout: page
---

The user profile is an entity that is part of the user aggregate:

```scala
package simbl.domain

import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)
extends Entity

object UserProfile extends EntityType[UserProfile]
```

`UserProfile` a simple case class that extends trait
`Entity`. `Entity` is a subclass of the empty trait `Embeddable`, which
we use to mark things that we want to embed in our persistent objects.

We also provide an `EntityType` in the `UserProfile` companion
object. `EntityType` is a subclass of `EType`, which is a type class
for an `Embeddable`. There's nothing fancy inside it, but we need this
class to make longevity aware of what kinds of things might be
embedded in our persistent objects.

The `UserProfile` has two members that are also `Embeddables`: `Uri`
and `Markdown`. They look pretty similar, so we'll only present `Uri`
here:

```scala
package simbl.domain

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class Uri(uri: String) extends ValueObject

object Uri extends ValueType[Uri]
```

`Uri` and `Markdown` are simple wrapper classes for strings, which
provide extra type safety, but are also places where we might add some
extra functionality in the future. For instance, the `Uri` constructor
might throw some kind of validation exception if the provided string
is not a valid URI. Both of these classes extend `ValueObject`, which
in turn extends the `Embeddable` trait. As you can see, we can freely
nest `Embeddables` within our `Persistent` classes.

{% assign prevTitle = "building the user aggregate" %}
{% assign prevLink = "user.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="username and email" %}
{% assign nextLink="keyvals.html" %}
{% include navigate.html %}
