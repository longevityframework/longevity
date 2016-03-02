---
title: repositories
layout: page
---

The API for a longevity repository provides basic CRUD persistence
operations for your aggregates, as follows:

{% gist sullivan-/3badba4e0178675d942d %}

Because all of the methods in `Repo` are potentially blocking, they
all return a [Scala
`Future`](http://www.scala-lang.org/api/current/index.html#scala.concurrent.Future). 

We will will discuss the three major `retrieve` methods in turn, but
it's helpful to cover a couple of points up front. First off, the
`PRef` is a super trait for both `Assoc` and `KeyVal`, so you can use
the `retrieve` and `retrieveOne` methods with both. Secondly, the
`retrieveOne` method is a simple wrapper method for `retrieve`, that
opens up the `Option[PState[R]]` for you. If the option is a `None`,
this will result in a `NoSuchElementException`.

<div class = "blue-side-bar">

There are two kinds of persistent refs: [[Assoc associations]] and
[[longevity.subdomain.root.KeyVal key values]]. we plan to integrate
these two types more in the future. In particular, it should be easier
to embed a key value of another aggregate in an entity, in place of
embedding an association.

</div>

{% assign prevTitle = "repositories" %}
{% assign prevLink = "." %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo.create" %}
{% assign nextLink = "create.html" %}
{% include navigate.html %}
