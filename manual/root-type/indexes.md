---
title: indexes
layout: page
---

An index in longevity is a requirement that certain forms of queries
on our aggregates should perform with low latency. For instance, if
there is an expectation that queries on `lastName` / `firstName`
should perform quickly, then we need to define an index, like so:

{% gist sullivan-/eaa0f96308d6f16a36c3 %}

An index like `lastFirstIndex` above will assure fast performance for
queries that filter on `lastName`, as well as for queries where
`lastName` is fixed and `firstName` is filtered. It will not assure
performance for a search on `firstName` alone.

Indexes are used by `Repo.findByQuery`, which is described in a [later
section](TODO.html).

{% assign prevTitle = "keys" %}
{% assign prevLink = "keys.html" %}
{% assign upTitle = "the root type" %}
{% assign upLink = "." %}
{% assign nextTitle = "the longevity context" %}
{% assign nextLink = "../context" %}
{% include navigate.html %}

