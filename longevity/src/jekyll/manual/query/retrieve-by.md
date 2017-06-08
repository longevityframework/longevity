---
title: retrieval by query
layout: page
---

Once we have constructed our query, we can iterate over the results using `Repo.queryToIterator`:

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

This approach is non-reactive; the resulting iterator will be blocking. If we are okay with
receiving the entire results at once, we can use `Repo.queryToFutureVec`, which returns a
`Future[Vector[PState[P]]]`:

```scala
import longevity.persistence.PState
import scala.concurrent.Future

val blog: Blog = getBlogFromSomewhere()

val recentPosts: Future[Vector[PState[BlogPost]]] = blogPostRepo.queryToFutureVec {
  import com.github.nscala_time.time.Imports._
  import BlogPost.queryDsl._
  import BlogPost.props._
  blogUri eqs blog.blogUri and postDate gt DateTime.now - 1.week
}
```

Clearly, this approach is not going to work for result sets that are
very large. For one thing, they are all going to have to fit into
memory at the same time. For another thing, you will have to wait for
the complete result set before you can start processing the
results. In these situations, you may want to stream the results
instead, as we discuss in the next section.

{% assign prevTitle = "limits and offsets" %}
{% assign prevLink  = "limit-offset.html" %}
{% assign upTitle   = "queries" %}
{% assign upLink    = "." %}
{% assign nextTitle = "stream by query" %}
{% assign nextLink  = "stream-by.html" %}
{% include navigate.html %}
