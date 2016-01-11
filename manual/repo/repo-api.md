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

{% assign prevTitle = "repositories" %}
{% assign prevLink = "." %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo.create" %}
{% assign nextLink = "create.html" %}
{% include navigate.html %}
