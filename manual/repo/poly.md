---
title: polymorphic repositories
layout: page
---

We we use [polymorphic persistent objects](../poly/persistents.html)
in our domain, we end up with a repository for the parent type, as
well as one for each of the derived subtypes. In our example, we have
a `User` trait with two inheriting subclasses, `Member` and
`Commenter`:

```scala
import longevity.subdomain.entity.entity.Entity
import longevity.subdomain.entity.EntityType

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)
extends Entity

object UserProfile extends EntityType[UserProfile]

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.DerivedPType
import longevity.subdomain.ptype.PolyPType

trait User extends Root {
  val username: String
  val email: Email
}

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

case class Member(
  username: String,
  email: Email,
  profile: UserProfile)
extends User

object Member extends DerivedPType[Member, User] {
  val polyPType = User
  object props {
    val tagline = prop[String]("profile.tagline")
  }
  object keys {
  }
  object indexes {
    val tagline = index(props.tagline)
  }
}

case class Commenter(
  username: String,
  email: Email)
extends User

object Commenter extends DerivedPType[Commenter, User] {
  val polyPType = User
  object keys {
  }
  object indexes {
  }
}

import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.entity.EntityTypePool
import longevity.subdomain.ptype.PTypePool

val subdomain = Subdomain(
  "blogging",
  PTypePool(User, Member, Commenter),
  EntityTypePool(UserProfile),
  ShorthandPool(Email, Markdown, Uri))
```

When we construct our [longevity context](../context), we can get
repositories for all three persistent types:

```scala
import longevity.context.LongevityContext
import longevity.context.Cassandra
import longevity.persistence.Repo

val context = LongevityContext(subdomain, Cassandra)

val userRepo: Repo[User] = context.repoPool[User]
val memberRepo: Repo[Member] = context.repoPool[Member]
val commenterRepo: Repo[Commenter] = context.repoPool[Commenter]
```

These three repositories all share the same backing store, so a
`Member` persisted by the `memberRepo` is accessible via the
`userRepo`, and vice-versa:

```scala
val member: Member = newMember()
val futureMemberState: Future[PState[Member]] = memberRepo.create(member)
val memberState: PState[Member] = Await(futureMemberState, Duration.Inf)

// we waited for the future to complete to make sure that the member
// was persisted. now we can look up the member with the other repo:

val userKeyVal: KeyVal[User] = User.keys.username(member.username)
val futureUserState: Future[PState[User]] = userRepo.retrieveOne(userKeyVal)
```

Notice how the final result of is a `PState[User]`. `PStates` are
invariant in their `Persistent` type parameter, so the `PStates` can
not be used interchangeably. `Keys`, `KeyVals`, `Assocs`, and
`Queries` are also all invariant in their type parameter, so in
general, you want to be working with one `Persistent` type at a
time.

That said, there is some flexibility added on to work around these
invariant type parameters. For example, a `PState[Member]` instance is
not also a `PState[User]`. But we can safely convert a
`PState[Member]` into a `PState[User]`, using method `widen`:

```scala
val memberState: PState[Member] = getMemberState()
val userState: PState[User] = memberState.widen[User]
```

And while a `Query[Member]` is not also a `Query[User]`, you can use
`User` properties when constructing your `Query[Member]`:

```scala
import Member.queryDsl._
val query: Query[Member] =
  User.props.username eq "someUsername" and
  Member.props.tagline lt "someTagline"
val queryResults = memberRepo.retrieveByQuery(query)
```

{% assign prevTitle = "repo.delete" %}
{% assign prevLink = "../repo/delete.html" %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "testing your subdomain" %}
{% assign nextLink = "../testing" %}
{% include navigate.html %}
