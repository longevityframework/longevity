---
title: optimistic locking
layout: page
---

TLDR: Add `longevity.optimisticLocking = true` to your config.

Optimistic locking, or [optimistic concurrency
control](https://en.wikipedia.org/wiki/Optimistic_concurrency_control),
is a technique used to prevent writes from overwriting changes from a
write issued on another thread. Suppose `User` has a method for
setting the title that disallows overwriting an existing title:

```scala
case class User(
  username: Username,
  title: Option[String])
extends longevity.subdomain.Persistent {

  def addTitle(newTitle: String): User = {
    if (title.nonEmpty) throw new ValidationException
    copy(title = Some(newTitle)
  }
}
```

Now let's suppose one thread is updating the user to have title `"Jr."`:

```scala
// thread A:
for {
  retrieved <- userRepo.retrieve(username)
  modified = retrieved.map(_.addTitle("Jr."))
  updated <- userRepo.update(modified)
} yield updated
```

and another thread is updating the title to `"Dr."`:

```scala
// thread B:
for {
  retrieved <- userRepo.retrieve(username)
  modified = retrieved.map(_.addTitle("Dr."))
  updated <- userRepo.update(modified)
} yield updated
```

If both of the calls to `Repo.retrieve` happen before the calls to
`Repo.update`, then we have a conflict. Without any sort of locking,
whichever `update` call happens last "wins", and the title from other
thread will be lost. With optimistic locking, the second update will
be kicked out with a
`longevity.exceptions.persistence.WriteConflictException`. The title
from the first update will prevail, and the other thread will have a
chance to respond to the `WriteConflictException`, such as by retrying
the operation, or returning a `409 Conflict` HTTP code.

In order to turn on optimistic locking in longevity, set the following
[configuration](config.html) variable:

```prop
longevity.optimisticLocking = true
```

Turning on optimistic locking will introduce a slight, but probably
negligible, performance penalty. For details on a specific back end,
please see the appropriate section in the [chapter on database
translation](../translation). You do not need to know how optimistic
locking works in order to use it, but we describe our implementation
briefly here for those who are interested.

We implement optimistic locking by storing a `rowVersion` value in
every database row. Every create or update operation will increment
the `rowVersion` by one.

The [persistent state](persistent-state.html) keeps track of the value
of `rowVersion` for the object it encloses. The repository methods
all return `PStates` with the `rowVersion` matching what is in the
database at that moment. `PState` methods `set` and `map` preserve the
`rowVersion`.

When `Repo` methods `update` or `delete` are called, the repository
qualifies the database write command that gets issued. The command
will only make a database change when the `rowVersion` in the
database matches the `rowVersion` in the `PState`. The repository
method checks the return value from the database to confirm the change
was made. If it was not, then there was a write conflict, and a
`WriteConflictException` is thrown.

{% assign prevTitle = "configuring your longevity context" %}
{% assign prevLink = "config.html" %}
{% assign upTitle = "the longevity context" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo pools" %}
{% assign nextLink = "repo-pools.html" %}
{% include navigate.html %}

