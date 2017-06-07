---
title: creating many aggregates at once
layout: page
---

`RepoPool.createMany` provides a convenient way to persist many
persistent objects at once. For example, suppose you were setting up
some sample data to use in your tests:

```scala
val john = User("smithy", "John Smith", "smithy@john-smith.ninja")
val frank = User("franky", "Francis Nickerson", "franky@john-smith.ninja")
val jerry = User("jerry", "Jerry Jones", "jerry@john-smith.ninja")

val blog = Blog(
  uri = BlogUri("http://blog.john-smith.ninja/"),
  title = "The Blogging Ninjas",
  description = "We try to keep things interesting blogging about ninjas.",
  authors = Set(john.username, frank.username))

val johnsPost = BlogPost(
  uri = BlogPostUri("johns_first_post"),
  title = "John's first post",
  content = "_work in progress_",
  postDate = DateTime.now,
  blog = blog.uri,
  authors = Set(john.username))

val franksPost = BlogPost(
  uri = BlogPostUri("franks_first_post"),
  title = "Frank's first post",
  content = "_work in progress_",
  postDate = DateTime.now,
  blog = blog.uri,
  authors = Set(frank.username))
```

We can now call `RepoPool.createMany` to create all our persistent
objects at once:

```scala
val createManyResult: Future[Seq[PState[_ <: Persistent]]] =
  repoPool.createMany(john, frank, blog, johnsPost, franksPost)
```

Like the `Repo` methods, `RepoPoo.createMany` takes an implicit
execution context argument. The easiest way to provide this is to
include `import scala.concurrent.ExecutionContext.Implicits.global` at
the top of the file.

{% assign prevTitle = "repo.create" %}
{% assign prevLink  = "create.html" %}
{% assign upTitle   = "the repository" %}
{% assign upLink    = "." %}
{% assign nextTitle = "repo.retrieve" %}
{% assign nextLink  = "retrieve.html" %}
{% include navigate.html %}
