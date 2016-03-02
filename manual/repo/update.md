---
title: repo.update
layout: page
---

Once we get our hands on a persistent state, we can use `PState.map`
to modify the aggregate:

    val retrieved: FPState[User] = userRepo.retrieveOne(User.usernameKey(username))
    val modified: FPState[User] = retrieved map { userState =>
      userState.map(_.copy(fullname = "John Smith Jr."))
    }

We can now persist our changes with `Repo.update`:

    val updated: FPState[User] = modified.map { userState =>
      userRepo.update(userState)
    }

Calling `Repo.update` may not result in a database call if the
persistent state is clean, and there are no changes that need to be
persisted. This is the only example of a repository API method that
does not necessarily result in a database call.

We can continue to manipulate the persistent state returned by
`Repo.update`, and pass it on to further calls to `update` or `delete`.

At present, there is nothing preventing you from modifying the
contents of a key value for an aggregate. Consequently, it is possible
for `update` to fail by attempting to put in a duplicate key
value. See the note at the bottom of the [page on
repo.create](create.html) for more information on duplicate keys.

The resulting `PState` result may well be different from the `PState`
taken as input. For example, the aggregate's revision counter may have
been updated. In this case, re-using (or continuing to use) the input
`PState` could result in an optimistic locking failure. In general,
you should consider a `PState` passed to `Repo.update` as no longer valid.

{% assign prevTitle = "limitations on cassandra queries" %}
{% assign prevLink = "cassandra-query-limits.html" %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo.delete" %}
{% assign nextLink = "delete.html" %}
{% include navigate.html %}
