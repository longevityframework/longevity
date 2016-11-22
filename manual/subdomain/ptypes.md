---
title: persistent types
layout: page
---

Every persistent object you define should have a corresponding
persistent type, or `PType`. Persistent types contain meta-information
about those entities. Let's look at a simple example to see how this
works. For example, here we have a simple `User` class:

```scala
case class User(
  username: String,
  firstName: String,
  lastName: String)
```

We need to build a corresponding `PType` for the `User`. By convention, we
designate the companion object as the `PType`. For now, we won't
provide any information in our persistent type, but we still need to
declare our [properties](../ptype/properties.html) and
[keys](../ptype/keys.html).  The easiest way to do this is by
providing empty `props` and `keys` objects inside the `PType`:

```scala
import longevity.subdomain.PType

object User extends PType[User] {
  object props {
  }
  object keys {
  }
}
```

You pass longevity a collection of your `PTypes` when [building your
subdomain](.), so that longevity is aware of them:

```scala
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

val subdomain = Subdomain("blogging", PTypePool(User))
```

All we need to do now is to [slap our `Subdomain` into a
`LongevityContext`](../context), and we are ready to start persisting
users, as we will see in a [later chapter](../repo).

{% assign prevTitle = "persistent objects" %}
{% assign prevLink  = "persistents.html" %}
{% assign upTitle   = "the subdomain" %}
{% assign upLink    = "." %}
{% assign nextTitle = "basic values" %}
{% assign nextLink  = "basics.html" %}
{% include navigate.html %}
