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

While we will discuss the three major `retrieve` methods in turn, note
that the two `retrieveOne` methods are just a shorthand for calling
the corresponding `retrieve` method, and unpacking the `Option` with a `get`.

<div class = "blue-side-bar">

We would like to find a way to combine the concepts of <a href =
"../associations">associations</a> and <a href =
"../root-type/keys.html">keys</a>, in order to simplify this API a
bit. We are considering making <code>KeyVal</code> extend
<code>Assoc</code>, but we'd like to mull over the ramifications of
that a bit more.

</div>

{% assign prevTitle = "repositories" %}
{% assign prevLink = "." %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo.create" %}
{% assign nextLink = "create.html" %}
{% include navigate.html %}
