---
title: repo.create
layout: page
---

`Repo.create` takes an unpersisted entity as argument, persists it,
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

If we want to persist an aggregate that has an association to another
aggregate, we need to retrieve an association from the `PState`:

```scala
val userAssoc: Assoc[User] = userState.assoc
val blog = Blog(
  uri = "http://blog.john-smith.ninja/",
  title = "The Blogging Ninjas",
  description = "We try to keep things interesting blogging about ninjas.",
  authors = Set(userAssoc))
```

Now we can persist the blog in the same manner:

```scala
val futureBlogState = blogRepo.create(blog)
```

If we want to persist many persistent entities at once, it can be
cumbersome to construct associations this way. In the next chapter, we
will look at a convenient way to persist many inter-related aggregates
at once.

`Repo.create` gives back a `PState`, which you can in turn manipulate
and pass to [`Repo.update`](repo-update.html) and
[`Repo.delete`](repo-delete.html).

When you attempt to create a persistent entity that has matching
values to an existing entity for a key defined in the `PType`, the
results are currently backend-specific. MongoDB on a single node will
throw a duplicate key exception. On multiple nodes, it may or may
not. On Cassandra, no such check is made. We
[plan](https://www.pivotaltracker.com/story/show/107958610) to give
the user finer control over this behavior in the future.

{% assign prevTitle = "the repo api" %}
{% assign prevLink = "repo-api.html" %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "creating many aggregates at once" %}
{% assign nextLink = "create-many.html" %}
{% include navigate.html %}
