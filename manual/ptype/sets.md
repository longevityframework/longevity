---
title: property sets
layout: page
---

If you look at the [API for
`PType`](http://longevityframework.github.io/longevity/scaladocs/longevity-latest/#longevity.subdomain.PType),
you will find that a `PType` has member `propSet: Set[Prop[P, _]]`.
The default constructor for `PType` uses Scala reflection to
recursively scan singleton object `props` for properties to build the
set. So in effect, a `PType` definition such as this:

```scala
import longevity.subdomain.PType

object User extends PType[User] {
  object props {
  }
  val keySet = emptyKeySet
}
```

Is functionally equivalent to this:

```scala
import longevity.subdomain.PType
import longevity.subdomain.ptype.Prop

object User extends PType[User] {
  override lazy val propSet = Set.empty[Prop[User, _]]
  val keySet = emptyKeySet
}
```

If you would rather eschew the conventional practice of defining your
properties in `object props`, you can always do so like this:

```scala
object User extends PType[User] {
  val usernameProp = prop[Username]("username")
  val emailProp = prop[Email]("email")
  val firstNameProp = prop[String]("firstName")
  val lastNameProp = prop[String]("lastName")

  override lazy val propSet = Set[Prop[User, _]](usernameProp, emailProp, firstNameProp, lastNameProp)
  val keySet = emptyKeySet
}
```

If you do not override `propSet`, and do not provide inner object
`props`, you will get a `NoPropsForPTypeException`.

Longevity needs access to the `propSet` in order to initialize your
database, among other things. But at the same time, you want to be
able to access the properties easily yourself, in order to build
queries, and define keys and indexes. So you don't want your
properties in a set. You want them each to have their own name.

We scan the inner object `props` for your convenience. We could ask
you to just build the set yourself, but then you would end up
repeating yourself to put the properties in a place where you could
access them individually. So we came up with the convention of putting
properties in an inner object, and following that convention allows us
to build the set for you.

{% assign prevTitle = "indexes" %}
{% assign prevLink = "indexes.html" %}
{% assign upTitle = "the persistent type" %}
{% assign upLink = "." %}
{% assign nextTitle = "subtype polymorphism" %}
{% assign nextLink = "../poly" %}
{% include navigate.html %}
