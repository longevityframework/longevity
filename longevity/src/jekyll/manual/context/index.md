---
title: the longevity context
layout: page
---

In broad terms, you pass in your `DomainModel` and a configuration,
and you get back a `LongevityContext`, which contains a variety of
tools for you to use. The main thing it gives you are the repositories
- one for each aggregate with all the basic persistence operations you
need to maintain your back-end store. But there are many other tools
there, most of which are most useful when writing tests.

- [Configuring your Longevity Context](config.html)
- [Repositories](repos.html)
- [Optimistic Locking](opt-lock.html)
- [Write Timestamps](write-timestamps.html)

{% assign prevTitle = "limitations on cassandra queries" %}
{% assign prevLink  = "../query/cassandra-query-limits.html" %}
{% assign upTitle   = "user manual" %}
{% assign upLink    = ".." %}
{% assign nextTitle = "configuring your longevity context" %}
{% assign nextLink  = "config.html" %}
{% include navigate.html %}

