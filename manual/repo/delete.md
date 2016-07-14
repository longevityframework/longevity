---
title: repo.delete
layout: page
---

Use `Repo.delete` to remove a persistent object from the database:

```scala
val userState: PState[User] = getUserState()
val deleteResult: Future[Deleted[User]] = userRepo.delete(userState)
```

The delete is complete when the future completes successfully.
For now, this is a hard delete, but see [PT
#106710604](https://www.pivotaltracker.com/story/show/106710604) for
support for soft deletes.

You cannot do much with the `Deleted`, but you can have at the
persisent object for old times sake:

```scala
deleteResult map { deleted =>
  val deletedUser: User = deleted.get
}
```

Of course, this value will not be particularly useful, as the
persisent object no longer exists.

<div class = "blue-side-bar">

We would like to support in-database deletes in the future, possibly
with something like <code>Repo.deleteByQuery</code>.

</div>

{% assign prevTitle = "repo.update" %}
{% assign prevLink = "repo-update.html" %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "polymorphic repositories" %}
{% assign nextLink = "poly.html" %}
{% include navigate.html %}
