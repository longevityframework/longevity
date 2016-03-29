---
title: retrieval by assoc
layout: page
---

An association to another persistent entity can be acquired by looking
up an `Assoc` found within some other entity. We can then look up the
associated entity using `Repo.retrieve`:

```scala
val blog: Future[PState[Blog]] = blogRepo.retrieveOne(Blog.uriKey(blogUri))
val author: Future[Option[PState[User]]] = blog flatMap { blogState =>
  val authorAssoc: Assoc[User] = blogState.get.authors.head
  userRepo.retrieve(authorAssoc)
}
```

You can also get an association to a persistent entity from the persistent
state of that entity. For instance, assigning a new author to a blog:

```scala
val updatedBlog: Future[PState[Blog]] = for {
  blogState <- blogRepo.retrieveOne(Blog.uriKey(blogUri))
  newAuthorState <- userRepo.retrieveOne(User.usernameKey(username))
  newAuthorAssoc = newAuthorState.assoc
  authorAdded = blogState.map(blog => blog.copy(authors = blog.authors + newAuthorAssoc))
  updated <- blogRepo.update(authorAdded)
} yield updated
```

{% assign prevTitle = "creating many aggregates at once" %}
{% assign prevLink = "create-many.html" %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "retrieval by key value" %}
{% assign nextLink = "retrieve-keyval.html" %}
{% include navigate.html %}
