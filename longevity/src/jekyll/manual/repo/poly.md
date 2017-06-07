---
title: persisting polymorphism
layout: page
---

When we use [polymorphic persistent objects](../poly/persistents.html)
in our domain, we end up with a repository for the parent type, as
well as one for each of the derived subtypes. In our example, we have
a `User` trait with two inheriting subclasses, `Member` and
`Commenter`:

```scala
import longevity.model.annotations.component
import longevity.model.annotations.derivedPersistent
import longevity.model.annotations.polyPersistent
import longevity.model.annotations.domainModel

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

@domainModel object domainModel
```

When we construct our [longevity context](../context), we can get
repositories for all three persistent types:

```scala
import longevity.context.LongevityContext
import longevity.persistence.Repo

val context = LongevityContext(domainModel)

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

val futureUserState: Future[PState[User]] = userRepo.retrieveOne(member.username)
```

Notice how the final result of is a `PState[User]`. `PStates` are
invariant in their `Persistent` type parameter, so the `PStates` can
not be used interchangeably. `Keys`, `KeyVals`, and `Queries` are also
all invariant in their type parameter, so in general, you want to be
working with one `Persistent` type at a time.

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
{% assign prevLink  = "../repo/delete.html" %}
{% assign upTitle   = "the repository" %}
{% assign upLink    = "." %}
{% assign nextTitle = "queries" %}
{% assign nextLink  = "../query" %}
{% include navigate.html %}
