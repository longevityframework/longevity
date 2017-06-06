---
title: persistent objects
layout: page
---

As longevity is primarily a persistence framework, the common currency
of the longevity API is the _persistent object_. Persistent objects
are part of your domain model, and they are also the _persistence unit_ -
something that you can create, retrieve, update or delete with a
longevity [repository](../repo).

Persistent objects are Scala traits and case classes that meet some basic criteria. Those criteria
are laid out in the chapters that follow. The basic idea is that they should form an [algebraic
data type](http://tpolecat.github.io/presentations/algebraic_types.html).

Here's a simple example:

```scala
case class User(
  username: String,
  firstName: String,
  lastName: String)
```

To tell longevity that this is a persistent object, we mark the class with a `@persistent`
annotation. This annotation requires a type parameter explicating the model:

```scala
import longevity.model.annotations.domainModel
import longevity.model.annotations.persistent

@domainModel trait DomainModel

@persistent[DomainModel]
case class User(
  username: String,
  firstName: String,
  lastName: String)
```

Be sure to declare your persistent class in the same package as, or in a sub-package of, the package
you declare your domain model. Assuming you don't fabricate your own
`longevity.model.ModelEv[DomainModel]`, if you declare your persistent class in another package, you
will get a compiler error - something about implicit model evidence not being found.

The `@persistent` annotation expands as follows:

```scala
import longevity.model.PType
import longevity.model.annotations.mprops

case class User(
  username: String,
  firstName: String,
  lastName: String)

@mprops object User extends PType[DomainModel, User]
```

The companion object has been made into a `PType[DomainModel, User]`, which will be used to build
the `ModelType[DomainModel]`, which collects all the different parts of your model.

The `@mprops` macro in turn creates an `object props` within your companion object, that allows us
to reflect over the fields of the `User` case class. We'll learn more about how this works in the
chapter on [properties](../ptype/properties.html). The expansion of `@mprops` looks something like
this:

```scala

import longevity.model.PType
import longevity.model.ptype.Prop

case class User(
  username: String,
  firstName: String,
  lastName: String)

object User extends PType[DomainModel, User] {
  object props {
    object username extends Prop[User, String]("username")
    object firstName extends Prop[User, String]("firstName")
    object lastName extends Prop[User, String]("lastName")
  }
}
```

{% assign prevTitle = "declaring a domain model" %}
{% assign prevLink  = "model.html" %}
{% assign upTitle   = "the domain model" %}
{% assign upLink    = "." %}
{% assign nextTitle = "basic values" %}
{% assign nextLink  = "basics.html" %}
{% include navigate.html %}
