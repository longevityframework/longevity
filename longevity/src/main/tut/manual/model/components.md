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

@component
case class FullName(
  firstName: String,
  lastName: String)

@persistent(keySet = emptyKeySet)
case class User(
  username: String,
  fullName: FullName)
```

The `@component` annotation creates a companion object that extends
`longevity.model.CType[FullName]`. If `FullName` already has a
companion object, it will be augmented to extend `CType`. Here is the
equivalent code without using annotations:

```scala
import longevity.model.CType

case class FullName(
  firstName: String,
  lastName: String)

object FullName extends CType[FullName]
```

You can put components in components, and components in
[collections](../collections.html), and collections in components. For
example:

```scala
import longevity.model.annotations.component
import longevity.model.annotations.persistent

@component
case class Email(email: String)

@component
case class EmailPreferences(
  primaryEmail: Email,
  emails: Set[Email])

@component
case class Address(
  street: String,
  city: String)

@persistent(keySet = emptyKeySet)
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

