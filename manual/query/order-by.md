---
title: ordered queries
layout: page
---

Once you have your query filter, you can specify an ordering of the
results using an `orderBy` clause. In the last section, we retrieved
all blog posts for a given blog from the past week, but it would be
nice to get them back in chronological order as well. This will return
the posts in ascending chronological order:

```scala
import longevity.persistence.PState
import scala.concurrent.Future

val blog: Blog = getBlogFromSomewhere()

val recentPosts: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery {
  import com.github.nscala_time.time.Imports._
  import BlogPost.queryDsl._
  import BlogPost.props._

  blogUri eqs blog.blogUri and postDate gt DateTime.now - 1.week orderBy postDate
}
```

Unfortunately, the DSL will not tolerate a line-break before the
`orderBy` operator. If you need a line-break, you might try variations
such as this:

```scala
{ blogUri eqs blog.blogUri and postDate gt DateTime.now - 1.week
} orderBy postDate
```

If we want them in descending order, we just change `postDate` to
`postDate.desc`:

```scala
import longevity.persistence.PState
import scala.concurrent.Future

val blog: Blog = getBlogFromSomewhere()

val recentPosts: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery {
  import com.github.nscala_time.time.Imports._
  import BlogPost.queryDsl._
  import BlogPost.props._

  blogUri eqs blog.blogUri and postDate gt DateTime.now - 1.week orderBy postDate.desc
}
```

If you prefer to to leave out the dot before `asc` or `desc`, you will
need to `import scala.language.postfixOps`:

```scala
import longevity.persistence.PState
import scala.concurrent.Future

val blog: Blog = getBlogFromSomewhere()

val recentPosts: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery {
  import com.github.nscala_time.time.Imports._
  import BlogPost.queryDsl._
  import BlogPost.props._
  import scala.language.postfixOps

  blogUri eqs blog.blogUri and postDate gt DateTime.now - 1.week orderBy postDate desc
}
```

If you want to specify multiple properties in your `orderBy` clause,
you will have to surround them in parentheses. Here's a silly example
where we retrieve all the blog posts in the last week from every
blog. We order them first by `blogUri`, and then by `postDate`

```scala
postDate gt DateTime.now - 1.week orderBy (blogUri, postDate.desc)
```

The `orderBy` clauses are fully processed by your back end, and
consequently, will affect the performance of your queries. As of now,
the back end will probably not be able to make use of your
[keys](../ptype/keys.html) to collect your query results in the right
order in place. This means that ordered queries that return a large
number of results are going to be costly. This will change when we
implement [partition keys](../translation/keys.html).

{% assign prevTitle = "query filters" %}
{% assign prevLink = "filters.html" %}
{% assign upTitle = "queries" %}
{% assign upLink = "." %}
{% assign nextTitle = "offsets and limits" %}
{% assign nextLink = "limit-offset.html" %}
{% include navigate.html %}
