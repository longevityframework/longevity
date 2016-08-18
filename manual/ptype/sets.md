---
title: prop sets, key sets, and index sets
layout: page
---

If you look at the [API for
`PType`](http://longevityframework.github.io/longevity/scaladocs/longevity-latest/#longevity.subdomain.ptype.PType),
you will find that a `PType` has members `propSet`, `keySet`, and
`indexSet`: sets of [properties](properties.html), [keys](keys.html),
and [indexes](indexes.html) of the appropriate type. The default
constructor for `PType` uses Scala reflection to scan singleton
objects `props`, `keys`, and `indexes` for elements of the appropriate
type to build these sets. So in effect, a `PType` definition such as
this:

```scala
import longevity.subdomain.ptype.RootType

object User extends RootType[User] {
  object props {
  }
  object keys {
  }
  object indexes {
  }
}
```

Is functionally equivalent to this:

```scala
import longevity.subdomain.ptype.Index
import longevity.subdomain.ptype.AnyKey
import longevity.subdomain.ptype.Prop
import longevity.subdomain.ptype.RootType

object User extends RootType[User] {
  override lazy val propSet = Set.empty[Prop[User, _]]
  override lazy val keySet = Set.empty[AnyKey[User]]
  override lazy val indexSet = Set.empty[Index[User]]
}
```

Type `AnyKey[User]` is a convenient way of saying "keys for `User`,
regardless of the type of the key value". It's a little less onerous
than saying `Key[User, V] forSome { type V <: KeyVal[User, V] }`.

If you would rather eschew the conventional practice of defining
your keys and indexes in the inner objects, you can always do so like
this:

```scala
object User extends RootType[User] {
  val usernameProp = prop[Username]("username")
  val emailProp = prop[Email]("email")
  val firstNameProp = prop[String]("firstName")
  val lastNameProp = prop[String]("lastName")

  val usernameKey = key(usernameProp)
  val emailKey = key(emailProp)
  val fullnameIndex = index(lastNameProp, firstNameProp)

  override lazy val propSet = Set[Prop[User, _]](usernameProp, emailProp, firstNameProp, lastNameProp)
  override lazy val keySet = Set[AnyKey[User]](usernameKey, emailKey)
  override lazy val indexSet = Set(fullnameIndex)
}
```

If you do not override `propSet`, or `keySet`, and do not provide
inner objects `props` or `keys`, you will get a
`NoPropsForPTypeException` or `NoKeysForPTypeException`,
respectively. (It would not be hard to have a macro that turns this
into a compile-time error.) If you do not override `indexSet` and do
not provide an inner object `indexes`, you will get no indexes. While
we expect nearly every persistent type to contain keys, we expect many
users will have no need for indexes. In NoSQL, indexes are vaguely
frowned upon. A preferred approach would be to maintain a [secondary
view table](http://martinfowler.com/bliki/CQRS.html) that will bypass
the need for an index.

Longevity needs access to these sets in order to initialize your
database, among other things. But at the same time, you need to be
able to access the keys easily yourself, in order to construct key
values to perform lookups. So you don't want your keys in a set. You
want each key to have its own name.

We scan the inner objects for your convenience. We could ask you to
just build the set yourself, but then you would end up repeating
yourself to put the keys in a place where you could access them
individually. So we came up with the convention of putting keys and
indexes in an inner object, and following that convention allows us to
build the sets for you.

{% assign prevTitle = "indexes" %}
{% assign prevLink = "indexes.html" %}
{% assign upTitle = "the persistent type" %}
{% assign upLink = "." %}
{% assign nextTitle = "subtype polymorphism" %}
{% assign nextLink = "../poly" %}
{% include navigate.html %}

