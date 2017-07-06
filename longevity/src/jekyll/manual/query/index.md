---
title: queries
layout: page
---

Longevity queries provide you with a way to retrieve multiple persistent objects. There are multiple
`Repo` methods for executing a query. Two of them, `queryToVector` and `queryToIterator`, are of
limited value, but depend only on Scala standard library classes. These are discussed in the
[Retrieval by Query](retrieve-by.html) section. Other methods return streams for third-party
streaming libraries. These are discussed in the [Stream by Query](stream-by.html) section.

To build queries, you can either use the query DSL, or you can assemble them by hand using the
classes and factory methods in package `longevity.model.query`. The query DSL is complete, so that
any query you can build by hand, you can also build with the DSL. We will focus our discussion here
on the DSL, since it is much more convenient to use. If you prefer to construct them by hand, please
refer to the [ScalaDocs](../../api/longevity/model/query/Query.html).

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

{% assign prevTitle = "persisting polymorphism" %}
{% assign prevLink  = "../repo/poly.html" %}
{% assign upTitle   = "user manual" %}
{% assign upLink    = ".." %}
{% assign nextTitle = "using the query dsl" %}
{% assign nextLink  = "dsl.html" %}
{% include navigate.html %}
