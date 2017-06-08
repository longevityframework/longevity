---
title: persisting polymorphism
layout: page
---

When we use [polymorphic persistent objects](../poly/persistents.html) in our domain, we end up with
a repository that performs operations on the parent type, as well as on all of the derived
subtypes. In this example, we have a `User` trait, and two inheriting subclasses, `Member` and
`Commenter`:

```scala
import longevity.model.annotations.derivedPersistent
import longevity.model.annotations.polyPersistent

@polyPersistent[DomainModel]
trait User {
  val username: Username
}

object User {
  implicit val usernameKey = key(props.username)
}

@derivedPersistent[DomainModel, User]
case class Member(
  username: Username,
  email: Email,
  numCats: Int)
extends User

object Member {
  implicit val emailKey = key(props.email)
}

@derivedPersistent[DomainModel, User]
case class Commenter(
  username: Username)
extends User
```

Note that `User` and `Member` each have their own key, on `username` and `email`, respectively.

When we construct our [longevity context](../context), we get a repository that can perform
persistence operations on both persistent classes. For example, we can create `Users`, `Members`,
and `Commenters`:

```scala
import longevity.context.LongevityContext
import longevity.persistence.Repo

val context = LongevityContext[DomainModel]()

val repo: Repo[DomainModel] = context.repo

val user: User = Member(Username("u1"), Email("e1"), 3)
val member: Member = Member(Username("u2"), Email("e2"), 5)
val commenter: Commenter = Commenter(Username("u3"))

import longevity.persistence.PState
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val fpUser: Future[PState[User]] = repo.create(user)
val fpMember: Future[PState[Member]] = repo.create(member)
val fpCommenter: Future[PState[Commenter]] = repo.create(commenter)
```

We can only retrieve based on the type that actually contains the key:

```scala
val retrievedUser: Future[Option[PState[User]]] =
  repo.retrieve[User](commenter.username)

val retrievedMember: Future[Option[PState[Member]]] =
  repo.retrieve[Member](member.email)
```

Trying `repo.retrieve[Commenter](commenter.username)` would produce a compiler error, because
`Commenter` does not have a key on `username`.

`PStates` are invariant in their `Persistent` type parameter, so the `PStates` can not be used
interchangeably. `Keys`, `KeyVals`, and `Queries` are also all invariant in their type parameter, so
in general, you want to be working with one `Persistent` type at a time.

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
import longevity.model.query.Query
import Member.queryDsl._

val query: Query[Member] =
  User.props.username eqs Username("u7") and
  Member.props.numCats gt 2
val queryResults = repo.queryToIterator(query)
```

{% assign prevTitle = "repo.delete" %}
{% assign prevLink  = "../repo/delete.html" %}
{% assign upTitle   = "the repository" %}
{% assign upLink    = "." %}
{% assign nextTitle = "queries" %}
{% assign nextLink  = "../query" %}
{% include navigate.html %}
