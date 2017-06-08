---
title: query spec
layout: page
---

The query spec is a useful tool for testing any
[queries](../query/retrieve-by.html) that you depend on in your
application. The way it works is this:

1. You subclass `longevity.test.QuerySpec`, providing the persistent
   type that you are querying on, and the longevity context you'd like
   to use.
2. When the test runs, the `QuerySpec` parent class will generate a
   set of entities and persist them all to the test database using the
   `testRepoPool`.
3. You construct queries to test in the subclass you created.
4. You call `QuerySpec.exerciseQuery(query: Query[P])` in your subclass.
5. `exerciseQuery` determines the expected results in memory, and
   compares it to the actual results returned by the query. It fails
   the test if there is a discrepancy.

For examples on how to use `QuerySpec`, please see the [longevity test
suite for
queries](https://github.com/longevityframework/longevity/tree/master/longevity/src/test/scala/longevity/integration/queries).
Take a look at the [source
code for `QuerySpec`](https://github.com/longevityframework/longevity/blob/master/longevity/src/main/scala/longevity/test/QuerySpec.scala)
as well. Unfortunately, the ScalaDoc for this class is quite useless,
as everything from ScalaTest is included there, and none of the
protected values from QuerySpec.

Some notes:

- You can override `protected val numEntities: Int` if you want to change
  the size of the set of test data.
- There are some other `vals` and `vars` there that you can use to
  help write your tests.
- There are also a handful of methods you can use to select persistent objects to help you generate
  reasonable queries: `randomP`, `medianPropVal`, and `orderStatPropVal`

Please note that the default test data generator will not work out of
the box in the face of constraint violations causing exceptions to be
thrown from your constructor. In this case, you need to provide custom
generators for your types. This is described in the section on
[enforcing constraints](constraints.html).

ScalaTest is an optional dependency in longevity, so you'll need to
declare a dependency on ScalaTest in your own project to use the query
specs. Please use ScalaTest version 3.0.1 or later.

{% assign prevTitle = "repo crud spec" %}
{% assign prevLink = "repo-crud-spec.html" %}
{% assign upTitle = "testing" %}
{% assign upLink = "." %}
{% assign nextTitle = "translating persistents to the database" %}
{% assign nextLink = "../translation" %}
{% include navigate.html %}
