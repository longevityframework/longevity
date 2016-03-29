---
title: entities
layout: page
---

Let's add a couple non-persistent entities to our user
aggregate. Let's say we want to give blog users the option to put up a
profile page, where they can put up a picture, a tagline, and a
description in [Markdown](https://en.wikipedia.org/wiki/Markdown). A
lot of information is stored within the user aggregate, so we want to
keep things organized and put the profile in a separate entity. We
define it in longevity like so:

```scala
import longevity.subdomain.Entity
import longevity.subdomain.EntityType

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
import longevity.subdomain.EntityTypePool
import longevity.subdomain.Subdomain

val subdomain = Subdomain("blogging", EntityTypePool(User, UserProfile))
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

{% assign prevTitle = "where not to construct your shorthand pools" %}
{% assign prevLink = "../subdomain/where-not.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "entities and value objects" %}
{% assign nextLink = "value-objects.html" %}
{% include navigate.html %}

