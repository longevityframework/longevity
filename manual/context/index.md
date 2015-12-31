---
title: the longevity context
layout: page
---

In broad terms, you pass in your `Subdomain` and a configuration, and
you get back a `LongevityContext`, which contains a variety of tools
for you to use. The main thing it gives you are the repositories - one
for each aggregate with all the basic persistence operations you need
to maintain your back-end store.

- [Repo Pools](repo-pools.html)
- [Persistent State](persistent-state.html)
- [Repositories](repositories.html)
- [Repo.create](repo-create.html)
- [Repo.retrieve](repo-retrieve.html)
- [Repo.retrieveByQuery](repo-query.html)
- [Repo.update](repo-update.html)
- [Repo.delete](repo-delete.html)
- Assoc.retrieve
- FPState and FOPState
- Testing Your Subdomain
- Enforcing Constraints
- Configuring your LongevityContext

{% assign prevTitle = "where not to construct your subdomain" %}
{% assign prevLink = "../subdomain/where-not.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "repo pools" %}
{% assign nextLink = "repo-pools.html" %}
{% include navigate.html %}

