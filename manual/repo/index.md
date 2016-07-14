---
title: repositories
layout: page
---

With the exception of `RepoPool.createMany`, all persistence
operations occur through the repository API. We will take a look at
the overall API, then discuss each of the API methods in turn.

- [The Repo API](repo-api.html)
- [Repo.create](create.html)
- [Creating Many Aggregates at Once](create-many.html)
- [Retrieval by Key Value](retrieve.html)
- [Retrieval by Query](query.html)
- [Stream by Query](stream.html)
- [Limitations on Cassandra Queries](cassandra-query-limits.html)
- [Repo.update](update.html)
- [Repo.delete](delete.html)
- [Polymorphic Repositories](poly.html)

{% assign prevTitle = "persistent state" %}
{% assign prevLink = "../context/persistent-state.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "the repo api" %}
{% assign nextLink = "repo-api.html" %}
{% include navigate.html %}
