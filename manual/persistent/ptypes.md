---
title: persistent types
layout: page
---

Every one of the traits in the `Entity` hierarchy has a corresponding
type-class. That type hierarchy looks like so:

- `EntityType`
  - `ValueType`
  - `PType`
    - `RootType`
    - `EventType`
    - `View`

You pass longevity a set of your `EntityTypes` when [building your
subdomain](../subdomain), so that longevity is aware of them. In
addition, the entity types contain meta-information about those
entities. `EntityTypes` themselves do not have to declare anything,
but `PTypes` must declare their keys - properties of an entity that
have to be unique among a collection of entities - and indexes -
properties of an entity that are used in querying.

We typically declare the entity's companion object as the
`EntityType`. The easiest way to define a `PType` with no keys or
indexes is by providing empty `keys` and `indexes` objects inside the
`PType`. For example, let's expand our user profile example from the
previous section to include the entity types:

```scala
import longevity.subdomain.Entity
import longevity.subdomain.EntityType
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

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)
extends Entity

object UserProfile extends EntityType[UserProfile]
```

{% assign prevTitle = "persistent and non-persistent entities" %}
{% assign prevLink = "non-persistent.html" %}
{% assign upTitle = "persistent entities" %}
{% assign upLink = "." %}
{% assign nextTitle = "building your subdomain" %}
{% assign nextLink = "../subdomain" %}
{% include navigate.html %}

