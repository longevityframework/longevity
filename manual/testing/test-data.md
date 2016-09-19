---
title: generating test data
layout: page
---

The longevity context provides a tool for producing test data:

```scala
val generator = longevityContext.testDataGenerator
```

You can construct test data with the `generate` method by supplying
the type that you want to generate:

```scala
val user: User = generator.generate[User]
```

All members are filled in with random values. For example, a `Boolean`
will be `true` about half the time, and `false` about half the
time. `Options` are non-empty about half the time. `Sets` and `Lists`
have between 0 and 4 elements. Strings are composed of 8 random
alphanumeric characters. You can use the generator to generate
`Embeddables`, `KeyVals`, `Options`, `Sets`, `Lists`, and even basic
types such as `String`. For instance:

```scala
val userId = generator.generate[UserId]
val bools = generator.generate[List[Boolean]]
val sampleString = generator.generate[String]
```

{% assign prevTitle = "in memory repositories" %}
{% assign prevLink = "in-mem-repos.html" %}
{% assign upTitle = "testing your subdomain" %}
{% assign upLink = "." %}
{% assign nextTitle = "enforcing constraints" %}
{% assign nextLink = "constraints.html" %}
{% include navigate.html %}
