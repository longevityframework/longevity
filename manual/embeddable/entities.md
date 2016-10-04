---
title: entities
layout: page
---

Entities represent components of other entities, or aggregate
roots. In UML class diagrams, entities would be
[components](http://creately.com/blog/diagrams/class-diagram-relationships/#Composition). The
life-cycle of a component is bound by the life-cycle of its
container. For example, it would not make sense to keep track of a
`UserProfile` for a deleted `User`. Nor would it make sense to create
a `UserProfile` for a `User` that didn't exist yet.

In Scala, we typically represent components as case classes nested
within other case classes. For example, here a `Computer` is built out
of components `Memory`, `CPU`, and `Display`:

```scala
case class Memory(gb: Int)
case class CPU(mhz: Double)
case class Display(resolution: Int)

case class Computer(memory: Memory, cpu: CPU, display: Display)
```

Let's add a couple entities to our user aggregate. Let's say we want
to give blog users the option to put up a profile page, where they can
put up a picture, a tagline, and a description. A lot of
information is stored within the user aggregate, so we want to keep
things organized and put the profile in a separate entity. We define
it in longevity like so:

```scala
import longevity.subdomain.embeddable.Entity

case class UserProfile(
  tagline: String,
  imageUri: String,
  description: String)
extends Entity
```

Let's add the profile to the user. They may not have created their
profile yet, so it should be optional:

```scala
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.PType

case class User(
  username: String,
  email: Email,
  profile: Option[UserProfile])
extends Persistent

object User extends PType[User] {
  object props {
  }
  object keys {
  }
}
```

Finally, we need to add our new entity into the `ETypePool`:

```scala
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.EntityType
import longevity.subdomain.PTypePool

val subdomain = Subdomain(
  "blogging",
  PTypePool(User),
  ETypePool(EntityType[UserProfile]))
```

{% assign prevTitle = "embeddables" %}
{% assign prevLink = "." %}
{% assign upTitle = "embeddables" %}
{% assign upLink = "." %}
{% assign nextTitle = "value objects" %}
{% assign nextLink = "value-objects.html" %}
{% include navigate.html %}

