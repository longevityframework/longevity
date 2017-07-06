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
directly translates into a longevity domain model. We are free to
use this controlled vocabulary in our domain, such as:

```scala
import longevity.model.annotations.persistent

@persistent[DomainModel]
case class Account(
  name: String,
  accountStatus: AccountStatus)
```

We just need to annotate the members of our controlled vocabulary with
`@polyComponent` and `@derivedComponent`:

```scala
import longevity.model.annotations.polyComponent
import longevity.model.annotations.derivedComponent

@polyComponent[DomainModel]
sealed trait AccountStatus

@derivedComponent[DomainModel, AccountStatus]
case object Active extends AccountStatus

@derivedComponent[DomainModel, AccountStatus]
case object Suspended extends AccountStatus

@derivedComponent[DomainModel, AccountStatus]
case object Cancelled extends AccountStatus
```

Support for `scala.Enumeration` would be a great addition, and is [on
our story
board](https://www.pivotaltracker.com/story/show/128589983). If you
are interested, this would be a great user contribution, and we would
be happy to support you in implementing it however we can.

{% assign prevTitle = "polymorphic persistents" %}
{% assign prevLink  = "persistents.html" %}
{% assign upTitle   = "subtype polymorphism" %}
{% assign upLink    = "." %}
{% assign nextTitle = "the longevity context" %}
{% assign nextLink  = "../context" %}
{% include navigate.html %}

