---
title: repo.delete
layout: page
---

Use `Repo.delete` to remove a persistent object from the database:

```scala
val userState: PState[User] = getUserState()
val deleteResult: Future[Deleted[User]] = repo.delete(userState)
```

Like most of the `Repo` API calls, `Repo.update` requires two implicit parameters: and
`ExecutionContext`, to perform the computation in a `scala.concurrent.Future`, and a
`longevity.model.PEv[M, P]`. This implicit evidence ensures that the type `P` is actually a
persistent class in the domain model.

The delete is complete when the future completes successfully. You
cannot do much with the `Deleted`, but you can have at the persisent
object for old times sake:

```scala
deleteResult.map { deleted =>
  val deletedUser: User = deleted.get
}
```

Of course, this value will not be particularly useful, as the persisent object no longer exists in
the database.

<div class = "blue-side-bar">

We would like to support in-database deletes in the future, possibly
with something like <code>Repo.deleteByQuery</code> or
<code>Repo.deleteByKeyVal</code>.

</div>

{% assign prevTitle = "repo.update" %}
{% assign prevLink  = "update.html" %}
{% assign upTitle   = "the repository" %}
{% assign upLink    = "." %}
{% assign nextTitle = "persisting polymorphism" %}
{% assign nextLink  = "poly.html" %}
{% include navigate.html %}
