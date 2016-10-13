---
title: retrieval by query
layout: page
---

Once we have constructed our query, we simply pass it to
`Repo.retrieveByQuery`, which returns a `Future[Seq[PState[P]]]`:

```scala
import longevity.persistence.PState
import scala.concurrent.Future

val blog: Blog = getBlogFromSomewhere()

val recentPosts: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery {
  import com.github.nscala_time.time.Imports._
  import BlogPost.queryDsl._
  import BlogPost.props._
  blogUri eqs blog.blogUri and postDate gt DateTime.now - 1.week
}
```

Here's an example of building the same query by hand:

```scala
import com.github.nscala_time.time.Imports._
import longevity.persistence.PState
import longevity.subdomain.query.Query
import longevity.subdomain.query.QueryFilter
import scala.concurrent.Future

val blog: Blog = getBlogFromSomewhere()

val queryResult: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery(
  Query(
    QueryFilter.and(
      QueryFilter.eqs(BlogPost.props.blogUri, blog.blogUri),
      QueryFilter.gt(BlogPost.props.postDate, DateTime.now - 1.week))))
```

Clearly, this approach is not going to work for result sets that are
very large. For one thing, they are all going to have to fit into
memory at the same time. For another thing, you will have to wait for
the complete result set before you can start processing the
results. In these situations, you may want to stream the results
instead, as we discuss in the next section.

{% assign prevTitle = "limits and offsets" %}
{% assign prevLink = "limit-offset.html" %}
{% assign upTitle = "queries" %}
{% assign upLink = "." %}
{% assign nextTitle = "stream by query" %}
{% assign nextLink = "stream-by.html" %}
{% include navigate.html %}
