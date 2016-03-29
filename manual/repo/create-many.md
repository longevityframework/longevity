---
title: creating many aggregates at once
layout: page
---

When creating networks of persistent entities together, you can use
`Assoc.apply` to build associations between them:

```scala
val john = User("smithy", "John Smith", "smithy@john-smith.ninja")
val frank = User("franky", "Francis Nickerson", "franky@john-smith.ninja")
val jerry = User("jerry", "Jerry Jones", "jerry@john-smith.ninja")

val blog = Blog(
  uri = "http://blog.john-smith.ninja/",
  title = "The Blogging Ninjas",
  description = "We try to keep things interesting blogging about ninjas.",
  authors = Set(Assoc(john), Assoc(frank)))

val johnsPost = BlogPost(
  uriPathSuffix = "johns_first_post",
  title = "John's first post",
  content = "_work in progress_",
  postDate = DateTime.now,
  blog = Assoc(blog),
  authors = Set(Assoc(john)))

val franksPost = BlogPost(
  uriPathSuffix = "franks_first_post",
  title = "Frank's first post",
  content = "_work in progress_",
  postDate = DateTime.now,
  blog = Assoc(blog),
  authors = Set(Assoc(frank)))
```

We call these kinds of associations "unpersisted". If you try to use
`Repo.create` to persist an aggregate with unpersisted assoc, the
operation will fail:

```scala
// fails with an AssocIsUnpersistedException:
blogRepo.create(blog)
```

Instead, we can use `RepoPool.createMany` to create all our aggregates
at once:

```scala
val createManyResult: Future[Seq[PState[_ <: Persistent]]] =
  repoPool.createMany(john, frank, blog, johnsPost, franksPost)
```

All the unpersisted associations will be recursively persisted. It
doesn't matter what order you pass them in; longevity will assure that
associated aggregates always get persisted first.

Like the `Repo` methods, `RepoPoo.createMany` takes an implicit
execution context argument. The easiest way to provide this is to
include `import scala.concurrent.ExecutionContext.Implicits.global` at
the top of the file.

Be careful, something like this is likely to fail with a duplicate key
error:

```scala
userRepo.create(john)
repoPool.createMany(frank, blog, johnsPost, franksPost)
```

Here, `createMany` will find an unpersisted assoc to `john` from both
`blog` and `johnsPost`, and will attempt to persist `john` a second
time. It always treats unpersisted assocs as aggregates to create. But
feel free to mix in persisted assocs as well. For example, if for
some odd reason we simply must create `john` first, we might do it
like this:

```scala
userRepo.create(john) flatMap { johnState =>
  repoPool.createMany(
    frank,
    blog.copy(authors = Set(
      johnState.assoc,
      Assoc(frank)))
    johnsPost.copy(authors = Set(
      johnState.assoc)),
    franksPost)
}
```

{% assign prevTitle = "repo.create" %}
{% assign prevLink = "repo-create.html" %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "retrieval by assoc" %}
{% assign nextLink = "retrieve-assoc.html" %}
{% include navigate.html %}
