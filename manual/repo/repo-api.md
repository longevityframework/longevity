---
title: repositories
layout: page
---

The API for a longevity repository provides basic CRUD persistence
operations for your aggregates, as follows:

TODO update this gist

{% gist sullivan-/3badba4e0178675d942d %}

Because all of the methods in `Repo` are potentially blocking, they
all return a [Scala
`Future`](http://www.scala-lang.org/api/current/index.html#scala.concurrent.Future). 

While we will discuss the three major `retrieve` methods in turn, note
that the two `retrieveOne` methods are just a shorthand for calling
the corresponding `retrieve` method, and unpacking the `Option` with a `get`.

{% assign prevTitle = "repositories" %}
{% assign prevLink = "." %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo.create" %}
{% assign nextLink = "create.html" %}
{% include navigate.html %}
