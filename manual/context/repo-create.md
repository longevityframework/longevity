---
title: repo.create
layout: page
---

`Repo.create` takes an unpersisted aggregate as argument, persists it,
and returns the persistent state:

    def create(unpersisted: R): Future[PState[R]]

For example, let's say we want to persist a user:

    val user = User("smithy", "John Smith", "smithy@john-smith.ninja")
    val futureUserState: Future[PState[User]] = userRepo.create(user)

We have to wait for the `Future` to complete before we can know that
the user has been persisted. For example:

    val userState: PState[User] = Await.result(futureUserState, 100 millis)

If we want to persist an aggregate that has an association to another
aggregate, we need to retrieve an association from the `PState`:

    val userAssoc: Assoc[User] = userState.assoc
    val blog = Blog(
      uri = "http://blog.john-smith.ninja/",
      title = "The Blogging Ninjas",
      description = "We try to keep things interesting blogging about ninjas.",
      authors = Set(userAssoc))

Now we can persist the blog in the same manner:

    val futureBlogState = blogRepo.create(blog)

If we want to persist many aggregates at once, it can be cumbersome to
construct associations this way. In the next chapter, we will look at
a convenient way to persist many inter-related aggregates at once.

`Repo.create` gives back a `PState`, which you can in turn manipulate
and pass to [`Repo.update`](repo-update.html) and
[`Repo.delete`](repo-delete.html).

When you attempt to create an aggregate that has matching values to an
existing aggregate for a key defined in the `RootEntity`, the results
are currently backend-specific. MongoDB on a single node will throw a
duplicate key exception. On multiple nodes, it may or may not. On
Cassandra, no such check is made. We
[plan](https://www.pivotaltracker.com/story/show/107958610) to give
the user finer control over this behavior in the future.

{% assign prevTitle = "repositories" %}
{% assign prevLink = "repositories.html" %}
{% assign upTitle = "the longevity context" %}
{% assign upLink = "." %}
{% assign nextTitle = "creating many aggregates at once" %}
{% assign nextLink = "create-many.html" %}
{% include navigate.html %}
