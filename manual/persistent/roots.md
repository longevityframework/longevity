---
title: aggregate roots
layout: page
---

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

Every root entity class needs a corresponding `RootType`
instance. By convention, we designate the companion object as the root
entity type. We put all your entity types into an `EntityTypePool`,
and pass it to the subdomain:

```scala
import longevity.subdomain.persistent.Root

case class User(
  username: String,
  firstName: String,
  lastName: String)
extends Root

import longevity.subdomain.ptype.RootType

object User extends RootType[User] {
  object keys {
  }
  object indexes {
  }
}

import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

val subdomain = Subdomain("blogging", PTypePool(User))
```

All we need to do now is to [slap our `Subdomain` into a
`LongevityContext`](../context), and we are ready to start persisting
users, as we will see in a [later chapter](../repo).

{% assign prevTitle = "kinds of subdomains" %}
{% assign prevLink = "kinds.html" %}
{% assign upTitle = "persistent objects" %}
{% assign upLink = "." %}
{% assign nextTitle = "basic properties" %}
{% assign nextLink = "../basics.html" %}
{% include navigate.html %}
