---
title: query filters
layout: page
---

The main part of your query is going to be the _query filter_, commonly
known as the "where clause". We saw a simple example of a query filter
in the previous section:

```scala
BlogPost.props.blogUri eqs blog.blogUri
```

This is a _relational filter_, which is the basic building block of most query filters. It consists
of three elements: a property, a relational operator, and a value that matches the type of the
property. The property typically belongs to the persistent type being queried, but if you are using
[polymorphic persistents](../poly/persistents.html), you can use properties from the parent
persistent type as well.

The relational operators are:

- `eqs`
- `neq`
- `lt`
- `lte`
- `gt`
- `gte`

Relational filters can be combined into _conditional filters_ with
conditional operators `and` and `or`. For instance, looking up all the
blog posts for a blog published in the last week:

```scala
import longevity.persistence.PState

val blog: Blog = getBlogFromSomewhere()

val recentPosts: Iterator[PState[BlogPost]] = repo.queryToIterator {
  import com.github.nscala_time.time.Imports._
  import BlogPost.queryDsl._
  import BlogPost.props._

  blogUri eqs blog.blogUri and postDate gt DateTime.now - 1.week
}
```

If you want to retrieve _every_ persistent in your collection, you can
use the special query filter `filterAll`:

```scala
repo.queryToIterator(BlogPost.queryDsl.filterAll)
```

[Keys](../ptype/keys.html) and [indexes](../ptype/indexes.html) will
aid query performance in an intuitive manner. For finer details on
just how your query will run, please see the chapter on how your
domain model is [translated to your NoSQL backend](../translation).

{% assign prevTitle = "using the query dsl" %}
{% assign prevLink = "dsl.html" %}
{% assign upTitle = "queries" %}
{% assign upLink = "." %}
{% assign nextTitle = "ordered queries" %}
{% assign nextLink = "order-by.html" %}
{% include navigate.html %}
