---
title: using the query dsl
layout: page
---

To get started using the query DSL, we will look at a simple example
of finding all blog posts for a blog. You simply need to import the
query DSL from your [persistent type](../ptype):

```scala
import longevity.persistence.PState
import scala.concurrent.Future

val blog: Blog = getBlogFromSomewhere()

import BlogPost.queryDsl._

val allPosts: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery(
  BlogPost.props.blogUri eqs blog.blogUri)
```

If you don't want the DSL wildcard imports to infect other parts of
your program, it is quite easy to localize them:

```scala
import longevity.persistence.PState
import scala.concurrent.Future

val blog: Blog = getBlogFromSomewhere()

val recentPosts: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery {
  import BlogPost.queryDsl._
  BlogPost.props.blogUri eqs blog.blogUri
}
```

{% assign prevTitle = "queries" %}
{% assign prevLink = "." %}
{% assign upTitle = "queries" %}
{% assign upLink = "." %}
{% assign nextTitle = "query filters" %}
{% assign nextLink = "filters.html" %}
{% include navigate.html %}
