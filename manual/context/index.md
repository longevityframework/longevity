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
- Using Your Repositories
  - Persistent State with `map` and `get`
  - CRUD Operations
  - Reactive with Futures
  - `Assoc.retrieve`
  - Optimistic Locking
- Something about Unpersisted Assocs
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

