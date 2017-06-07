---
title: repo.create
layout: page
---

`Repo.create` takes an unpersisted object as argument, persists it,
and returns the persistent state:

```scala
def create(unpersisted: P): Future[PState[P]]
```

For example, let's say we want to persist a user:

```scala
val user = User("smithy", "John Smith", "smithy@john-smith.ninja")
val futureUserState: Future[PState[User]] = userRepo.create(user)
```

We have to wait for the `Future` to complete before we can know that
the user has been persisted. For example:

```scala
val userState: PState[User] = Await.result(futureUserState, 100 millis)
```

`Repo.create` gives back a `PState`, which you can in turn manipulate
and pass to [`Repo.update`](repo-update.html) and
[`Repo.delete`](repo-delete.html).

When you attempt to create a persistent object that has matching
values to an existing entity for a key defined in the `PType`, the
results are currently backend-specific. For MongoDB, in the absence of
a [primary key](../ptype/primary-keys.html), a
`DuplicateKeyValException` will be thrown. If you have a primary
key, a `DuplicateKeyValException` will only be thrown for the
primary key, and only when the primary key is not hashed.

On Cassandra, no check for duplicate key values is made. We
[plan](https://www.pivotaltracker.com/story/show/107958610) to give
the user finer control over this behavior in the future.

{% assign prevTitle = "persistent state wrappers" %}
{% assign prevLink  = "pstate-wrappers.html" %}
{% assign upTitle   = "the repository" %}
{% assign upLink    = "." %}
{% assign nextTitle = "creating many aggregates at once" %}
{% assign nextLink  = "create-many.html" %}
{% include navigate.html %}
