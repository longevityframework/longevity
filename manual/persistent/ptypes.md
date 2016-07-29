---
title: persistent types
layout: page
---

Every one of the traits in the `Persistent` hierarchy has a
corresponding type-class. That type hierarchy looks like so:

- `PType`
  - `RootType`
  - `EventType`
  - `View`

The persistent types contain meta-information about those
entities. Let's look at a simple example to see how this works.

We're building a blogging application, and our earliest user stories
to implement revolve around creating and setting up user accounts. The
first part of our domain that we want to flesh out is the User
aggregate. We start out by giving the user three basic fields:
`username`, `firstName`, and `lastName`. When we create our aggregate
root, we need to mark it as a `Root`:

```scala
import longevity.subdomain.persistent.Root

case class User(
  username: String,
  firstName: String,
  lastName: String)
extends Root
```

Now we need to build a corresponding `RootType`. By convention, we
designate the companion object as the `PType`. For now, we won't
provide any information in our persistent type, but we still need to
declare our [properties](../ptype/properties.html),
[keys](../ptype/keys.html), and [indexes](../ptype/indexes.html).
The easiest way to do this is by providing empty `props`, `keys`, and
`indexes` objects inside the `PType`:

```scala
import longevity.subdomain.ptype.RootType

object User extends RootType[User] {
  object props {
  }
  object keys {
  }
}
```

You pass longevity a collection of your `PTypes` when [building your
subdomain](../subdomain.html), so that longevity is aware of them:

```scala
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

val subdomain = Subdomain("blogging", PTypePool(User))
```

All we need to do now is to [slap our `Subdomain` into a
`LongevityContext`](../context), and we are ready to start persisting
users, as we will see in a [later chapter](../repo).

{% assign prevTitle = "kinds of persistent objects" %}
{% assign prevLink = "kinds.html" %}
{% assign upTitle = "persistent objects" %}
{% assign upLink = "." %}
{% assign nextTitle = "basic properties" %}
{% assign nextLink = "../basics.html" %}
{% include navigate.html %}
