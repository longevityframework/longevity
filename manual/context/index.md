---
title: the longevity context
layout: page
---

In broad terms, you pass in your `Subdomain` and a configuration, and
you get back a `LongevityContext`, which contains a variety of tools
for you to use. The main thing it gives you are the repositories - one
for each aggregate with all the basic persistence operations you need
to maintain your back-end store.

- [Persistence Strategy](pstrat.html)
- [Configuring your Longevity Context](config.html)
- [Repo Pools](repo-pools.html)
- [Persistent State](persistent-state.html)

{% assign prevTitle = "where not to construct your subdomain" %}
{% assign prevLink = "../subdomain/where-not.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "persistence strategy" %}
{% assign nextLink = "pstrat.html" %}
{% include navigate.html %}

