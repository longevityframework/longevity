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
directly translates into a longevity-enabled subdomain. We are free to
use this controlled vocabulary in our domain, such as:

```scala
import longevity.subdomain.PType

case class Account(
  name: String,
  accountStatus: AccountStatus)

object Account extends PType[Account] {
  object keys {
  }
  object indexes {
  }
}
```

We just need to add the members of our controlled vocabulary to the
`CTypePool`:

```scala
import longevity.subdomain.DerivedCType
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool
import longevity.subdomain.PolyCType
import longevity.subdomain.Subdomain

val subdomain = Subdomain(
  "accounts",
  PTypePool(Account),
  CTypePool(
    PolyCType[AccountStatus],
    DerivedCType[Active.type, AccountStatus],
    DerivedCType[Suspended.type, AccountStatus],
    DerivedCType[Cancelled.type, AccountStatus]))
```

Having to list all the members of the controlled vocabulary here is
less than ideal, and something we want to address. It is presently
covered by the same [user
story](https://www.pivotaltracker.com/story/show/127406543) on our
story board.

Support for `scala.Enumeration` would be a great addition, and is [on
our story board as
well](https://www.pivotaltracker.com/story/show/128589983). If you are
interested, this would be a great user contribution, and we would be
happy to support you in implementing it however we can.

{% assign prevTitle = "polymorphic persistents" %}
{% assign prevLink  = "persistents.html" %}
{% assign upTitle   = "subtype polymorphism" %}
{% assign upLink    = "." %}
{% assign nextTitle = "repositories" %}
{% assign nextLink  = "../repo" %}
{% include navigate.html %}

