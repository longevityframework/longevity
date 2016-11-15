---
title: repo.update
layout: page
---

Once we get our hands on a persistent state, we can use `PState.map`
to modify the aggregate:

```scala
val username = Username("smithy")
val retrieved: FPState[User] = userRepo.retrieveOne(username)
val modified: FPState[User] = retrieved map { userState =>
  userState.map(_.copy(fullname = "John Smith Jr."))
}
```

We can now persist our changes with `Repo.update`:

```scala
val updated: FPState[User] = modified.map { userState =>
  userRepo.update(userState)
}
```

All this looks much nicer using [for
comprehensions](http://docs.scala-lang.org/tutorials/tour/sequence-comprehensions.html):

```scala
val username = Username("smithy")
val updated: FPState[User] = for {
  retrieved <- userRepo.retrieveOne(username)
  modified = retrieved.map(_.copy(fullname = "John Smith Jr."))
  updated <- userRepo.update(modified)
} yield updated
```

We can continue to manipulate the persistent state returned by
`Repo.update`, and pass it on to further calls to `update` or `delete`.

Unless the key value is for a [partition
key](../ptype/partition-keys.html), there is nothing at present that
prevents you from modifying the contents of a key value for a
persistent object. Consequently, it is possible for `update` to fail
by attempting to put in a duplicate key value. See the note at the
bottom of the [section on repo.create](create.html) for more
information on duplicate keys.

The resulting `PState` result may well be different from the `PState`
taken as input. For example, the aggregate's revision counter may have
been updated. In this case, re-using (or continuing to use) the input
`PState` could result in an optimistic locking failure. In general,
you should consider a `PState` passed to `Repo.update` as no longer valid.

{% assign prevTitle = "repo.retrieve" %}
{% assign prevLink  = "retrieve.html" %}
{% assign upTitle   = "repositories" %}
{% assign upLink    = "." %}
{% assign nextTitle = "repo.delete" %}
{% assign nextLink  = "delete.html" %}
{% include navigate.html %}
