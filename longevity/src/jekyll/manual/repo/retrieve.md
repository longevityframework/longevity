---
title: repo.retrieve
layout: page
---

You can look up a persistent object from the database using the [keys](../ptype/keys.html) you
defined in your `PType`. All you need is a [key value](../model/key-values.html). One way to do this
is to construct one yourself, like so:

```scala
val username = Username("smithy")
val f: Future[Option[PState[User]]] = repo.retrieve[User](username)
```

If you look at the [API
docs](../../api/longevity/persistence/Repo.html#retrieve[P]:Repo.this.Retrieve[P]) for
`Repo.retrieve`, you will see that the method above is actually divided into two parts. The
`repo.retrieve[User]` returns a `Repo.Retrieve[User]` object. We then call `apply` on this object -
the `(username)` part in the above code sample - which actually does the work. The reason why the
API is divided up in this way is to prevent you from having to supply `Username` as a type
parameter. Otherwise the call would look like this:

```scala
repo.retrieve[User, Username](username) // this won't compile
```

Due to the way that type inference in Scala is done, there is no way that the `User` type can be
inferred in this case. (The presence of the `User` type on the left-hand side of the assignment
doesn't help.)

The `Repo.Retrieve[P].apply` method takes three implicit arguments. Like `Repo.create`, we require
an `ExecutionContext`, as well as a `PEnv[M, P]`, to ensure that the persistent class is actually
part of the domain model. The retrieve method also requires an implicit
`longevity.model.ptype.Key[M, P, V]` argument, to assure that the key value supplied actually
matches up with a key defined in the [persistent type](../ptype) for `P`. This is why we typically
define our [keys](../ptype/keys.html) as implicit values.

Take note that `Repo.retrieve` returns an _optional_ `PState`, since there is never a guarantee that
a key value will match an existing persistent object. If you feel confident that the persistent
object does exist, you can use `Repo.retrieveOne` instead. This is a simple wrapper method for
`retrieve`, that opens up the `Option[PState[P]]` for you. If the option is a `None`, this will
result in a `NoSuchElementException`.

You can also get a key value from an
[aggregation](http://aviadezra.blogspot.com/2009/05/uml-association-aggregation-composition.html) in
a related entity. For example, a `BlogPost` aggregates a set of authors for that post:

```scala
case class BlogPost(
  uri: BlogPostUri,
  title: String,
  slug: Option[Markdown] = None,
  content: Markdown,
  labels: Set[String] = Set(),
  postDate: DateTime,
  blog: BlogUri,
  authors: Set[Username])
```

Let's retrieve all the authors for a blog post:

```scala
def getAuthorsForPost(blogPost: BlogPost): Future[Seq[PState[User]]] = {
  val futures: Seq[Future[PState[User]]] = blogPost.authors.toSeq.map { author =>
    repo.retrieveOne(author)
  }
  Future.sequence(futures)
}
```

Once you get back your `PState`, you can of course use it to examine
the persistent object itself with `PState.get`. You can modify it with
`PState.map`, and you can pass the state on to
[`Repo.update`](repo-update.html) or
[`Repo.delete`](repo-delete.html).

`Repo.retrieve` will always result in a database call. Longevity will
not cache versions for you and pull them from the cache. Not caching
provides a guarrantee that the retrieved object is up to date with
the latest state of the database, and reduces the chances of a write
collision. We may revisit this in the future, but we do not consider
it an excessive burden on the longevity user to employ their own
cache, if need be.

{% assign prevTitle = "creating many aggregates at once" %}
{% assign prevLink  = "create-many.html" %}
{% assign upTitle   = "the repository" %}
{% assign upLink    = "." %}
{% assign nextTitle = "repo.update" %}
{% assign nextLink  = "update.html" %}
{% include navigate.html %}
