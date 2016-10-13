---
title: offsets and limits
layout: page
---

Longevity also supports offset and limit clauses for your queries. For
example, suppose we want to display users by username, and we want to
page the results. We construct a method that will return a query that
will retrieve a single page of results:

```scala
def userPageQuery(pageNum: Int, pageSize: Int): Query[User] = {
  import User.queryDsl._
  import User.props._
  filterAll orderBy username offset pageNum * pageSize limit pageSize
}
```

The `orderBy`, `offset`, and `limit` clauses are all optional, and can
occur whether or not the other two clauses appear. But the clauses
have to occur in the right order, so variations such as these will not
work:

```scala
  filterAll offset pageNum * pageSize limit pageSize orderBy username

  filterAll orderBy username limit pageSize offset pageNum * pageSize
```

The `offset` and `limit` clauses will affect performance. A large
offset will see performance degrade, as the back end generally has to
collect all the results with an offset less than the offset you
supplied. The limit clause will only improve performace for unordered
queries. Once we implement [partition keys](../translation/keys.html),
it should also improve performance of ordered queries in some
circumstances.

{% assign prevTitle = "ordered queries" %}
{% assign prevLink = "order-by.html" %}
{% assign upTitle = "queries" %}
{% assign upLink = "." %}
{% assign nextTitle = "retrieval by query" %}
{% assign nextLink = "retrieve-by.html" %}
{% include navigate.html %}
