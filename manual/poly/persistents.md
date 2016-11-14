---
title: polymorphic persistents
layout: page
---

We can use polymorphism with our [persistent types](../persistent) as
well. For example, let's say our blogging system has two kinds of
users: members and commenters. Only members can have a user
profile. With persistent types, we inherit from `PolyPType` and
`DerivedPType`, in place of the `PolyType` and `DerivedType` we used
for entities.

```scala
import longevity.subdomain.Embeddable

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)
extends Embeddable

import longevity.subdomain.DerivedPType
import longevity.subdomain.Persistent
import longevity.subdomain.PolyPType

trait User extends Persistent {
  val username: String
  val email: Email
}

object User extends PolyPType[User] {
  object props {
  }
  object keys {
  }
}

case class Member(
  username: String,
  email: Email,
  profile: UserProfile)
extends User

object Member extends DerivedPType[Member, User] {
  object props {
  }
  object keys {
  }
}

case class Commenter(
  username: String,
  email: Email)
extends User

object Commenter extends DerivedPType[Commenter, User] {
  object props {
  }
  object keys {
  }
}
```

Notice how `User`, `Member`, and `Commenter` all have their own sets
of properties, keys, and indexes. We could, for example, put in a
[key](../ptype/keys.html) on `User.username`, and
[indexes](../ptype/indexes.html) on `User.email` and
`Member.profile.tagline`, like so:

```scala
import longevity.subdomain.DerivedPType
import longevity.subdomain.PolyPType

object User extends PolyPType[User] {
  object props {
    val username = prop[String]("username")
    val email = prop[Email]("email")
  }
  object keys {
    val username = key(props.username)
  }
  object indexes {
    val email = index(props.email)
  }
}

object Member extends DerivedPType[Member, User] {
  object props {
    val tagline = prop[String]("profile.tagline")
  }
  object keys {
  }
  object indexes {
    val tagline = index(props.tagline)
  }
}
```

When you construct your [longevity context](../context), you will get
a [repository](../repo) for for each of your derived persistent types,
along with one for the parent `PolyPType`. All these repositories will
share the same backing store, so a `Member` persisted by the
`Repo[User]` will be retrievable via the `Repo[Member]`, and
vice-versa. Keys and indexes declared in `object User` will apply to
all types of `Users`, whereas keys and indexes declared in `object
Member` will only apply to members. For more information, see the
section on [polymorphic repositories](../repo/poly.html).

{% assign prevTitle = "polymorphic components" %}
{% assign prevLink  = "components.html" %}
{% assign upTitle   = "subtype polymorphism" %}
{% assign upLink    = "." %}
{% assign nextTitle = "controlled vocabularies" %}
{% assign nextLink  = "cv.html" %}
{% include navigate.html %}

