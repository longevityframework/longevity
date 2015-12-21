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

{% assign prevTitle = "persistent state" %}
{% assign prevLink = "persistent-state.html" %}
{% assign upTitle = "the longevity context" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo.create" %}
{% assign nextLink = "repo-create.html" %}
{% include navigate.html %}
