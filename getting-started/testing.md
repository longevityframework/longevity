---
title: testing crud operations
layout: page
---

Before we wrap up, we'd like to point out a useful tool that you can
pull out of the `LongevityContext`: the `RepoCrudSpec`. This will test
all of your CRUD operations for all of your persistent types against a
test database. It's trivial to set up, as you can see
in <a href="#code/src/test/scala/simbl/SimblRepoCrudSpec.scala">SimblRepoCrudSpec.scala</a>. There's
also a little framework for testing queries, and you can see an
example of that in
<a href="#code/src/test/scala/simbl/BlogPostQuerySpec.scala">BlogPostQuerySpec.scala</a>. You
can run these for yourself using the `Test` tab in
the left margin, or by running `sbt test` from the
console.

{% assign prevTitle = "exercising the api" %}
{% assign prevLink = "exercising.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="exercises for the reader" %}
{% assign nextLink="exercises.html" %}
{% include navigate.html %}
