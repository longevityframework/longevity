---
title: controlled vocabularies
layout: page
---

There are two major approaches to doing controlled vocabularies in
Scala: using
[`scala.Enumeration`](http://www.scala-lang.org/api/current/index.html#scala.Enumeration),
or using a sealed trait with case objects. Here's a controlled
vocabulary for account status that uses the latter approach:

```scala
sealed trait AccountStatus

case object Active extends AccountStatus

case object Suspended extends AccountStatus

case object Cancelled extends AccountStatus
```

Because longevity supports case objects and polymorphism, this pattern
directly translates into a longevity-enabled subdomain. We simply have
to mark the top of the hierarchy as `Embeddable`, and create `ETypes`
for the four types in the hierarchy:

```scala
import longevity.subdomain.embeddable.DerivedType
import longevity.subdomain.embeddable.Embeddable
import longevity.subdomain.embeddable.PolyType

sealed trait AccountStatus extends Embeddable

object AccountStatus extends PolyType[AccountStatus]

case object Active extends AccountStatus

object Active_Type extends DerivedType[Active.type, AccountStatus] {
  val polyType = AccountStatus
}

case object Suspended extends AccountStatus

object Suspended_Type extends DerivedType[Suspended.type, AccountStatus] {
  val polyType = AccountStatus
}

case object Cancelled extends AccountStatus

object Cancelled_Type extends DerivedType[Cancelled.type, AccountStatus] {
  val polyType = AccountStatus
}
```

Creating all these `ETypes` is straightforward, but a bit verbose. We
[have plans](https://www.pivotaltracker.com/story/show/127406543) to
save you from writing this kind of boilerplate in the future.

Note that our `ETypes` for the members of the controlled vocabulary
have to be named differently from our case objects, or we would get a
name clash. If you like, you can get around this by using a case class
with an empty parameter list instead. For instance:

```scala
case class Active() extends AccountStatus

object Active extends DerivedType[Active, AccountStatus] {
  val polyType = AccountStatus
}
```

But attempting something like this:

```scala
case object Active
extends DerivedType[Active.type, AccountStatus]
with AccountStatus {
  val polyType = AccountStatus
}
```

Produces an "illegal cyclic reference" compiler error.

In any event, we are now free to use this controlled vocabulary in our
domain, such as:

```scala
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class Account(
  name: String,
  accountStatus: AccountStatus)
extends Root

object Account extends RootType[Account] {
  object keys {
  }
  object indexes {
  }
}
```

We just need to add the members of our controlled vocabulary to the
`ETypePool`:

```scala
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.ptype.PTypePool

val subdomain = Subdomain(
  "accounts",
  PTypePool(Account),
  ETypePool(AccountStatus, Active_Type, Suspended_Type, Cancelled_Type))
```

Having to list all the members of the controlled vocabulary here is,
once again, less than ideal, and something we want to address. It is
presently covered by the same [user
story](https://www.pivotaltracker.com/story/show/127406543) on our
story board.

Support for `scala.Enumeration` would be a great addition, and is [on
our story board as
well](https://www.pivotaltracker.com/story/show/128589983). If you are
interested, this would be a great user contribution, and we would be
happy to support you in implementing it however we can.

{% assign prevTitle = "polymorphic persistents" %}
{% assign prevLink = "persistents.html" %}
{% assign upTitle = "subtype polymorphism" %}
{% assign upLink = "." %}
{% assign nextTitle = "the longevity context" %}
{% assign nextLink = "../context" %}
{% include navigate.html %}

