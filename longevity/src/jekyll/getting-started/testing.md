---
title: testing crud operations
layout: page
---

Before we wrap up, we'd like to point out a useful tool that you can
pull out of the `LongevityContext`: the `RepoCrudSpec`. This will test
all of your CRUD operations for all of your persistent types against a
test database. It's trivial to set up, as you can see
in `SimblRepoCrudSpec.scala`

```scala
package simbl

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class SimblRepoCrudSpec extends Suites(
  TestSimblContext.longevityContext.repoCrudSpec)
```

There's also a little framework for testing queries, and you can see
an example of that in `BlogPostQuerySpec.scala`:

```scala
package simbl

import longevity.test.QuerySpec
import scala.concurrent.ExecutionContext.Implicits.global
import simbl.domain.BlogPost
import simbl.domain.SimblDomainModel

class BlogPostQuerySpec extends QuerySpec[SimblDomainModel, BlogPost](
  TestSimblContext.longevityContext) {

  lazy val sample = randomP

  behavior of "BlogPost.queries.recentPosts"
  it should "produce the expected results" in {
    exerciseQuery(BlogPost.queries.recentPosts(sample.blog))
  }

}
```

You can run these for yourself using `sbt test` from the command line.

{% assign prevTitle = "exercising the api" %}
{% assign prevLink = "api.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle = "exercises for the reader" %}
{% assign nextLink = "exercises.html" %}
{% include navigate.html %}
