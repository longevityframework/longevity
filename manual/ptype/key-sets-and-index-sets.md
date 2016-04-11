---
title: key sets and index sets
layout: page
---

If you look at the [API for
`PType`](http://longevityframework.github.io/longevity/scaladocs/longevity-latest/#longevity.subdomain.ptype.PType),
you will find that a `PType` has members `keySet` and `indexSet`: sets
of [keys](keys.html) and [indexes](indexes.html) of the appropriate
type. The default constructor for `PType` uses Scala reflection to
scan singleton objects `keys` and `indexes` for elements of the
appropriate type to build these sets. So in effect, a `PType`
definition such as this:

```scala
import longevity.subdomain.ptype.RootType

object User extends RootType[User] {
  object keys {
  }
  object indexes {
  }
}
```

Is equivalent to this:

```scala
import longevity.subdomain.ptype.Index
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.RootType

object User extends RootType[User] {
  override lazy val keySet = Set.empty[Key[User]]
  override lazy val indexSet = Set.empty[Index[User]]
}
```

If you would rather eschew the conventional practice of defining
your keys and indexes in the inner objects, you can always do so like
this:

```scala
object User extends PType[User] {
  val usernameKey = key("username")
  val emailKey = key("email")
  val fullnameIndex = index("lastName", "firstName")

  override val keySet = Set(usernameKey, emailKey)
  override val indexSet = Set(fullnameIndex)
}
```

If you do not override `keySet` or `indexSet`, and do not provide
inner objects `keys` or `indexes`, you will get a
`NoKeysForPTypeException` or `NoIndexesForPTypeException`,
respectively. (It would not be hard to have a macro that turns this
into a compile-time error.)

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
{% assign nextTitle = "the longevity context" %}
{% assign nextLink = "../context" %}
{% include navigate.html %}
