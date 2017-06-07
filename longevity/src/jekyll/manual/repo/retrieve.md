---
title: repo.retrieve
layout: page
---

You can look up any persistent entities from the database using the
[keys](../ptype/keys.html) you defined in your `PType`. All you need
is a `KeyVal`. One way to do this is to construct one yourself, like
so:

```scala
val userRepo = repoPool[User]
val username = Username("smithy")
val futureUserState: Future[Option[PState[User]]] =
  userRepo.retrieve(username)
```

Take note that `Repo.retrieve` returns an _optional_ `PState`, since
there is never a guarantee that a `KeyVal` will match an existing
persistent object. If you feel confident that the persistent object
does exist, you can use `Repo.retrieveOne` instead. This is a simple
wrapper method for `retrieve`, that opens up the `Option[PState[P]]`
for you. If the option is a `None`, this will result in a
`NoSuchElementException`.

You can also get a `KeyVal` from an
[aggregation](http://aviadezra.blogspot.com/2009/05/uml-association-aggregation-composition.html)
in a related entity. For example, a `BlogPost` aggregates a set of
authors for that post:

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
extends Persistent
```

Let's retrieve all the authors for a blog post:

```scala
def getAuthorsForPost(blogPost: BlogPost): Future[Seq[PState[User]]] = {
  val futures: Seq[Future[PState[User]]] = blogPost.authors.toSeq.map { author =>
    userRepo.retrieveOne(author)
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
