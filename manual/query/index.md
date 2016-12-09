---
title: queries
layout: page
---

Longevity queries provide you with a way to retrieve multiple
persistent objects, either [all at once](retrieve-by.html), or [in a
stream](stream-by.html).

To build queries, you can either use the query DSL, or you can
assemble them by hand using the classes and factory methods in package
`longevity.model.query`. The query DSL is complete, so that any
query you can build by hand, you can also build with the DSL. We will
focus our discussion here on the DSL, since it is much more convenient
to use. If you prefer to construct them by hand, please refer to the
[ScalaDocs](http://longevityframework.github.io/longevity/scaladocs/longevity-latest/index.html#longevity.model.query.Query).

Please note that, due to the limited nature of Cassandra `SELECT`
statements, many of the queries discussed in this chapter will not
work when using a Cassandra back end. Please see the section on
[Cassandra limitations](cassandra-query-limits.html) for the details.

This chapter is organized as follows:

- [Using the Query DSL](dsl.html)
- [Query Filters](filters.html)
- [Ordered Queries](order-by.html)
- [Offsets and Limits](limit-offset.html)
- [Retrieval by Query](retrieve-by.html)
- [Stream by Query](stream-by.html)
- [Limitations on Cassandra Queries](cassandra-query-limits.html)

{% assign prevTitle = "polymorphic repositories" %}
{% assign prevLink = "../repo/poly.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "using the query dsl" %}
{% assign nextLink = "dsl.html" %}
{% include navigate.html %}
