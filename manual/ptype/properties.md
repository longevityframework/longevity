---
title: properties
layout: page
---

In our `PType`, when we talk about the fields of the `Persistent`
type, we talk about properties, or `Props`. Properties follow a path
from the root, and take on the type of that field in the root. Here
are some examples:

```scala
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.entity.Entity
import longevity.subdomain.entity.EntityType
import longevity.subdomain.entity.EntityTypePool
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.PTypePool
import longevity.subdomain.ptype.RootType

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)
extends Entity

object UserProfile extends EntityType[UserProfile]

case class User(
  username: String,
  email: Email,
  profile: UserProfile)
extends Root

object User extends RootType[User] {

  // fully typed:
  val profileDescription: longevity.subdomain.ptype.Prop[User, Markdown] =
    prop[Markdown]("profile.description")

  // brief:
  val usernameProp = prop[String]("username")

  object keys {
  }
  object indexes {
  }
}

val subdomain = Subdomain(
  "blogging",
  PTypePool(User),
  EntityTypePool(UserProfile),
  ShorthandPool(Email, Markdown, Uri))
```

You need to specify the type of the property yourself, and longevity
will check that the type is correct when the property is created.

We recommend bundling them in a `props` object within your root class, like
so:

```scala
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.PTypePool
import longevity.subdomain.ptype.RootType

case class User(
  username: String,
  firstName: String,
  lastName: String,
  email: String)
extends Root

object User extends RootType[User] {
  object props {
    val username = prop[String]("username")
    val firstName = prop[String]("firstName")
    val lastName = prop[String]("lastName")
    val email = prop[String]("email")
  }
  object keys {
  }
  object indexes {
  }
}

val subdomain = Subdomain("blogging", PTypePool(User))
```

Your properties can also descend into entites included in your
persistent object. For instance, when our `User` has an embedded
`EmailPreferences` entity, we can form a `Prop` reaching in to the
`primaryEmail`, like so:

```scala
case class EmailPreferences(
  primaryEmail: Email,
  emails: Set[Email])
extends Entity

object EmailPreferences extends EntityType[EmailPreferences]

case class User(
  username: String,
  emails: EmailPreferences,
  addresses: Set[Address],
  profile: Option[UserProfile])
extends Root

object User extends RootType[User] {
  object props {
    val username = prop[String]("username")
    val email = prop[Email]("emails.primaryEmail")
  }
  object keys {
  }
  object indexes {
  }
}
```

In principle, properties could map through any path from the persisten
object, and have a wide variety of types. In practice, we currently
support properties with [basic types](../subdomain/basics.html),
[shorthand types](../subdomain/shorthands.html), and
[associations](../subdomain/associations.html). While you can descend
into any contained entity, you currently cannot use collection types
such as `Option`, `Set`, and `List`. We plan to continue to expand the
supported properties types in the future.

Properties are used to build [keys](keys.html),
[indexes](indexes.html), and [queries](../repo/query.html). We're
interested in looking at a more fluent API for creating properties,
possibly using
[dynamics](http://www.scala-lang.org/api/current/index.html#scala.Dynamic)
and/or
[macros](http://docs.scala-lang.org/overviews/macros/overview.html).

{% assign prevTitle = "the persistent type" %}
{% assign prevLink = "." %}
{% assign upTitle = "the persistent type" %}
{% assign upLink = "." %}
{% assign nextTitle = "keys" %}
{% assign nextLink = "keys.html" %}
{% include navigate.html %}

