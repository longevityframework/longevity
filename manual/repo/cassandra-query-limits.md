---
title: limitations on cassandra queries
layout: page
---

Due to the limitations on querying in Cassandra, there are a number of
queries that can be built that will not work on Cassandra. Here's
the rundown.

While a Mongo query on a column that is not mentioned in a
[key](../root-type/keys.html) or an [index](../root-type/indexes.html)
may not perform well, it will still run. A Cassandra query on any
column that is not mentioned in a key or index will fail to run.

Queries using the _not-equals_ operator are not supported by Cassandra.

Queries using the _or_ operator are not supported by Cassandra.

{% assign prevTitle = "retrieval by query" %}
{% assign prevLink = "query.html" %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo.update" %}
{% assign nextLink = "update.html" %}
{% include navigate.html %}
