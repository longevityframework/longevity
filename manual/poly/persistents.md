---
title: polymorphic persistents
layout: page
---

We can use polymorphism with our [persistent
objects](../subdomain/persistents.html) as well. For example, let's
say our blogging system has two kinds of users: members and
commenters. Only members can have a user profile. Here, we use
annotations `@polyPersistent` and `@derivedPersistent` in place of
`@persistent`:

```scala
import longevity.subdomain.annotations.component
import longevity.subdomain.annotations.derivedPersistent
import longevity.subdomain.annotations.polyPersistent

@component
case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)

@polyPersistent(keySet = emptyKeySet)
trait User {
  val username: Username
  val email: Email
}

@derivedPersistent[User](keySet = emptyKeySet)
case class Member(
  username: Username,
  email: Email,
  profile: UserProfile)
extends User

@derivedPersistent[User](keySet = emptyKeySet)
case class Commenter(
  username: Username,
  email: Email)
extends User
```

The non-annotation equivalent is as follows:

```scala
import longevity.subdomain.CType
import longevity.subdomain.DerivedPType
import longevity.subdomain.PolyPType

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)

object UserProfile extends CType[UserProfile]

trait User {
  val username: Username
  val email: Email
}

object User extends PolyPType[User] {
  object props {
    // ...
  }
  val keySet = emptyKeySet
}

case class Member(
  username: Username,
  email: Email,
  profile: UserProfile)
extends User

object Member extends DerivedPType[Member, User] {
  object props {
    // ...
  }
  val keySet = emptyKeySet
}

case class Commenter(
  username: Username,
  email: Email)
extends User

object Commenter extends DerivedPType[Commenter, User] {
  object props {
    // ...
  }
  val keySet = emptyKeySet
}
```

Notice how `User`, `Member`, and `Commenter` all have their own
properties, keys, and indexes. We could, for example, put in a
[key](../ptype/keys.html) on `User.username`, and
[indexes](../ptype/indexes.html) on `User.email` and
`Member.profile.tagline`, like so:

```scala
import longevity.subdomain.annotations.derivedPersistent
import longevity.subdomain.annotations.polyPersistent

@polyPersistent(
  keySet = Set(key(props.username)),
  indexSet = Set(index(props.email)))
trait User {
  val username: Username
  val email: Email
}

@derivedPersistent[User](
  keySet = emptyKeySet,
  indexSet = Set(index(props.profile.tagline)))
case class Member(
  username: Username,
  email: Email,
  profile: UserProfile)
extends User
```

When you construct your [longevity context](../context), you will get
a [repository](../repo) for for each of your `@derivedPersistents`,
along with one for the parent `@polyPersistent`. All these
repositories will share the same backing store, so a `Member`
persisted by the `Repo[User]` will be retrievable via the
`Repo[Member]`, and vice-versa. Keys and indexes declared in `object
User` will apply to all types of `Users`, whereas keys and indexes
declared in `object Member` will only apply to members. For more
information, see the section on [polymorphic
repositories](../repo/poly.html).

{% assign prevTitle = "polymorphic components" %}
{% assign prevLink  = "components.html" %}
{% assign upTitle   = "subtype polymorphism" %}
{% assign upLink    = "." %}
{% assign nextTitle = "controlled vocabularies" %}
{% assign nextLink  = "cv.html" %}
{% include navigate.html %}

