---
title: generating test data
layout: page
---

The longevity context provides a tool for producing test data:

```scala
val generator = longevityContext.testDataGenerator
```

You can construct test data with the `generateP` method by supplying the type of the persistent
object that you want to generate:

```scala
val user: User = generator.generateP[User]
```

All members are filled in with random values. For example, a `Boolean` will be `true` about half the
time, and `false` about half the time. `Options` are non-empty about half the time. `Sets` and
`Lists` have vary in size from 0 on up. Strings are composed of 12 random alphanumeric characters.
Currently, you can use the generator to generate persistent components, `Ints` and `Strings`. For
instance:

```scala
val sampleInt = generator.generateInt
val sampleString = generator.generateString
```

In the future, we will probably expand the `TestDataGenerator` to generate more data types.

{% assign prevTitle = "in memory repositories" %}
{% assign prevLink = "in-mem-repos.html" %}
{% assign upTitle = "testing your domain model" %}
{% assign upLink = "." %}
{% assign nextTitle = "enforcing constraints" %}
{% assign nextLink = "constraints.html" %}
{% include navigate.html %}
