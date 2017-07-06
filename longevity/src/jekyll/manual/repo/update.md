---
title: repo.update
layout: page
---

Once we get our hands on a persistent state, we can use `PState.map`
to modify the aggregate:

```scala
val username = Username("smithy")
val retrieved: Future[PState[User]] = repo.retrieveOne[User](username)
val modified: Future[PState[User]] = retrieved map { userState =>
  userState.map(_.copy(fullname = "John Smith Jr."))
}
```

Like most of the `Repo` API calls, `Repo.update` requires two implicit parameters: and
`ExecutionContext`, to perform the computation in a `scala.concurrent.Future`, and a
`longevity.model.PEv[M, P]`. This implicit evidence ensures that the type `P` is actually a
persistent class in the domain model.

We can now persist our changes with `Repo.update`:

```scala
val updated: Future[PState[User]] = modified.map { userState =>
  repo.update(userState)
}
```

All this looks much nicer using [for
comprehensions](http://docs.scala-lang.org/tutorials/FAQ/yield.html):

```scala
val username = Username("smithy")
val updated: Future[PState[User]] = for {
  retrieved <- repo.retrieveOne(username)
  modified  =  retrieved.map(_.copy(fullname = "John Smith Jr."))
  updated   <- repo.update(modified)
} yield updated
```

We can continue to manipulate the persistent state returned by
`Repo.update`, and pass it on to further calls to `update` or `delete`.

Unless the key value is for a [primary key](../ptype/primary-keys.html), there is nothing at present
that prevents you from modifying the contents of a key value for a persistent object. Consequently,
it is possible for `update` to fail by attempting to put in a duplicate key value. See the note at
the bottom of the [section on repo.create](create.html) for more information on duplicate keys.

The resulting `PState` result may well be different from the `PState`
taken as input. For example, the aggregate's revision counter may have
been updated. In this case, re-using (or continuing to use) the input
`PState` could result in an optimistic locking failure. In general,
you should consider a `PState` passed to `Repo.update` as no longer valid.

{% assign prevTitle = "repo.retrieve" %}
{% assign prevLink  = "retrieve.html" %}
{% assign upTitle   = "the repository" %}
{% assign upLink    = "." %}
{% assign nextTitle = "repo.delete" %}
{% assign nextLink  = "delete.html" %}
{% include navigate.html %}
