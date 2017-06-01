---
title: repositories
layout: page
---

With the exception of
[`RepoPool.createMany`](../context/create-many.html), all persistence
operations occur through the repository API. We will take a look at
the overall API, then discuss each of the API methods in
turn. Repository queries are discussed in the [following
chapter](../query).

- [Persistent State](persistent-state.html)
- [Persistent State Wrappers](pstate-wrappers.html)
- [The Repo API](repo-api.html)
- [Repo.create](create.html)
- [Retrieval by Key Value](retrieve.html)
- [Repo.update](update.html)
- [Repo.delete](delete.html)
- [Polymorphic Repositories](poly.html)

{% assign prevTitle = "controlled vocabularies" %}
{% assign prevLink  = "../poly/cv.html" %}
{% assign upTitle   = "user manual" %}
{% assign upLink    = ".." %}
{% assign nextTitle = "the persistent state" %}
{% assign nextLink  = "persistent-state.html" %}
{% include navigate.html %}
