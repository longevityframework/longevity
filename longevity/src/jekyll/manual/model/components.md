---
title: persistent components
layout: page
---

Persistent components are a way of nesting case classes inside of your
persistent objects. They never get persisted on their own, but rather
as part of some other persistent type in your domain model.

For example, let's suppose we want to group the user's `firstName` and
`lastName` fields into a `FullName` case class:

```scala
import longevity.model.annotations.component
import longevity.model.annotations.persistent

@component[DomainModel]
case class FullName(
  firstName: String,
  lastName: String)

@persistent[DomainModel](keySet = emptyKeySet)
case class User(
  username: String,
  fullName: FullName)
```

Be sure to declare your component class in the same package as, or in a sub-package of, the package
you declare your domain model. Assuming you don't fabricate your own
`longevity.model.ModelEv[DomainModel]`, if you declare your persistent class in another package, you
will get a compiler error - something about implicit model evidence not being found.

The `@component[DomainModel]` annotation creates a companion object that extends
`longevity.model.CType[DomainModel, FullName]`. If `FullName` already has a companion object, it
will be augmented to extend `CType`. Here is the equivalent code without using annotations:

```scala
import longevity.model.CType

case class FullName(
  firstName: String,
  lastName: String)

object FullName extends CType[DomainModel, FullName]
```

You can put components in components, and components in
[collections](../collections.html), and collections in components. For
example:

```scala
import longevity.model.annotations.component
import longevity.model.annotations.persistent

@component[DomainModel]
case class Email(email: String)

@component[DomainModel]
case class EmailPreferences(
  primaryEmail: Email,
  emails: Set[Email])

@component[DomainModel]
case class Address(
  street: String,
  city: String)

@persistent[DomainModel](keySet = emptyKeySet)
case class User(
  username: String,
  emails: EmailPreferences,
  addresses: Set[Address])
```

{% assign prevTitle = "collections" %}
{% assign prevLink  = "collections.html" %}
{% assign upTitle   = "the domain model" %}
{% assign upLink    = "." %}
{% assign nextTitle = "key values" %}
{% assign nextLink  = "key-values.html" %}
{% include navigate.html %}

