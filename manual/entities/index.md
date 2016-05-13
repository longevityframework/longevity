---
title: entities
layout: page
---

Entities represent components of one or more of our [persistent
objects](../persistent). In UML class diagrams, entities would be
[components](http://creately.com/blog/diagrams/class-diagram-relationships/#Composition). The
life-cycle of a component is bound by the life-cycle of its
container. For example, it would not make sense to keep track of a
`UserProfile` for a deleted `User`. Nor would it make sense to create
a `UserProfile` for a `User` that didn't exist yet.

In Scala, we typically represent components as case classes nested
within other case classes. For example, here a `Computer` is build out
of components `Memory`, `CPU`, and `Display`:

```scala
case class Memory(gb: Int)
case class CPU(mhz: Double)
case class Display(resolution: Int)

case class Computer(memory: Memory, cpu: CPU, display: Display)
```

In terms of persistence, an `Entity` is something that never gets
persisted on its own; it gets persisted as part of some containing
`Persistent` object. So we would never explicitly persist a `Memory`
object. `Memory` objects would only be persisted as part of persisting
a `Computer`.

Let's add a couple entities to our user aggregate. Let's say we want
to give blog users the option to put up a profile page, where they can
put up a picture, a tagline, and a description in
[Markdown](https://en.wikipedia.org/wiki/Markdown). A lot of
information is stored within the user aggregate, so we want to keep
things organized and put the profile in a separate entity. We define
it in longevity like so:

```scala
import longevity.subdomain.entity.Entity
import longevity.subdomain.entity.EntityType

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)
extends Entity

object UserProfile extends EntityType[UserProfile]
```

Let's add the profile to the user. They may not have created their
profile yet, so it should be optional:

```scala
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class User(
  username: String,
  email: Email,
  profile: Option[UserProfile])
extends Root

object User extends RootType[User] {
  object keys {
  }
  object indexes {
  }
}
```

You need to add all your new entities into the `EntityTypePool`:

```scala
import longevity.subdomain.Subdomain
import longevity.subdomain.entity.EntityTypePool
import longevity.subdomain.ptype.PTypePool

val subdomain = Subdomain("blogging", PTypePool(User), EntityTypePool(UserProfile))
```

You can put entities in entities, and entities into [supported
collection types](collections.html) `Option`, `Set` and `List`,
collections into entities, use shorthands freely, etc. For example:

```scala
case class EmailPreferences(
  primaryEmail: Email,
  emails: Set[Email])
extends Entity

case class User(
  username: String,
  emails: EmailPreferences,
  addresses: Set[Address],
  profile: Option[UserProfile])
extends Root
```

{% assign prevTitle = "collections" %}
{% assign prevLink = "../collections.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "entities and value objects" %}
{% assign nextLink = "value-objects.html" %}
{% include navigate.html %}

