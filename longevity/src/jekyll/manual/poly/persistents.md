---
title: polymorphic persistents
layout: page
---

We can use polymorphism with our [persistent
objects](../model/persistents.html) as well. For example, let's
say our blogging system has two kinds of users: members and
commenters. Only members can have a user profile. Here, we use
annotations `@polyPersistent` and `@derivedPersistent` in place of
`@persistent`:

```scala
import longevity.model.annotations.component
import longevity.model.annotations.derivedPersistent
import longevity.model.annotations.polyPersistent

@component[DomainModel]
case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)

@polyPersistent[DomainModel]
trait User {
  val username: Username
  val email: Email
}

@derivedPersistent[DomainModel, User]
case class Member(
  username: Username,
  email: Email,
  profile: UserProfile)
extends User

@derivedPersistent[DomainModel, User]
case class Commenter(
  username: Username,
  email: Email)
extends User
```

The non-annotation equivalent is as follows:

```scala
import longevity.model.CType
import longevity.model.DerivedPType
import longevity.model.PolyPType

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)

object UserProfile extends CType[DomainModel, UserProfile]

trait User {
  val username: Username
  val email: Email
}

object User extends PolyPType[DomainModel, User] {
  object props {
    // ...
  }
}

case class Member(
  username: Username,
  email: Email,
  profile: UserProfile)
extends User

object Member extends DerivedPType[DomainModel, Member, User] {
  object props {
    // ...
  }
}

case class Commenter(
  username: Username,
  email: Email)
extends User

object Commenter extends DerivedPType[DomainModel, Commenter, User] {
  object props {
    // ...
  }
}
```

Notice how `User`, `Member`, and `Commenter` all have their own
properties, keys, and indexes. We could, for example, put in a
[key](../ptype/keys.html) on `User.username`, and
[indexes](../ptype/indexes.html) on `User.email` and
`Member.profile.tagline`, like so:

```scala
import longevity.model.annotations.derivedPersistent
import longevity.model.annotations.polyPersistent

@polyPersistent[DomainModel]
trait User {
  val username: Username
  val email: Email
}

object User {
  implicit val usernameKey = key(props.username)
  override val indexSet = Set(index(props.email))
}

@derivedPersistent[DomainModel, User]
case class Member(
  username: Username,
  email: Email,
  profile: UserProfile)
extends User

object Member {
  override val indexSet = Set(index(props.profile.tagline))
}
```

When you construct your [longevity context](../context), you will be able to use
[repository](../repository) for for each of your `@derivedPersistents`, along with one for the
parent `@polyPersistent`. These persistent classes will share the same backing store, so a `Member`
persisted as a `User` will be retrievable as a `Member`, and vice-versa. Keys and indexes declared
in `object User` will apply to all types of `Users`, whereas keys and indexes declared in `object
Member` will only apply to members. For more information, see the section on [polymorphic
repositories](../repo/poly.html).

TODO fix up this link when chapter name changes

{% assign prevTitle = "polymorphic components" %}
{% assign prevLink  = "components.html" %}
{% assign upTitle   = "subtype polymorphism" %}
{% assign upLink    = "." %}
{% assign nextTitle = "controlled vocabularies" %}
{% assign nextLink  = "cv.html" %}
{% include navigate.html %}

