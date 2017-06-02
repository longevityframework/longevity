---
title: persistent objects
layout: page
---

As longevity is primarily a persistence framework, the common currency
of the longevity API is the _persistent object_. Persistent objects
are part of your domain model, and they are also the _persistence unit_ -
something that you can create, retrieve, update or delete with a
longevity [repository](../repo).

Persistent objects are Scala case classes that meet some basic
criteria. Those criteria are laid out in the chapters that follow.

Here's a simple example:

```scala
case class User(
  username: String,
  firstName: String,
  lastName: String)
```

To tell longevity that this is a persistent object, we mark the class with a `@persistent`
annotation. This annotation requires a type parameter explicating the model. We also need to provide
a `keySet` to the `@persistent` annotation. We'll describe key sets in detail in a [later
chapter](../ptype/keys.html). For now, we can provide an empty set of keys, like so:

```scala
import longevity.model.annotations.domainModel
import longevity.model.annotations.persistent

@domainModel trait DomainModel

@persistent[DomainModel](keySet = emptyKeySet)
case class User(
  username: String,
  firstName: String,
  lastName: String)
```

Be sure to declare your persistent class in the same package as, or in a sub-package of, the package
you declare your domain model. Assuming you don't fabricate your own
`longevity.model.ModelEv[DomainModel]`, if you declare your persistent class in another package, you
will get a compiler error - something about implicit model evidence not being found.

Here is how you would do the same thing without the annotation:

```scala
import longevity.model.PType

case class User(
  username: String,
  firstName: String,
  lastName: String)

object User extends PType[DomainModel, User] {
  object props {
    // ...
  }
  lazy val keySet = emptyKeySet
}
```

Details on the contents of that inner `object props` are discussed in the chapter on
[properties](../ptype/properties.html).

{% assign prevTitle = "declaring a domain model" %}
{% assign prevLink  = "model.html" %}
{% assign upTitle   = "the domain model" %}
{% assign upLink    = "." %}
{% assign nextTitle = "basic values" %}
{% assign nextLink  = "basics.html" %}
{% include navigate.html %}
