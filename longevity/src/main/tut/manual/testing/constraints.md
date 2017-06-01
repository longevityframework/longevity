---
title: enforcing constraints
layout: page
---

The test data generator described in the [previous
section](test-data.html) will handle most anything you put in your
domain. The only thing we cannot handle out of the box is when
exceptions are thrown in the constructors of your
[persistent](../model/persistents.html) and
[component](../model/components.html) objects.

A class constructor is a great place to enforce domain constraints,
such as requiring that an email has an at sign (`@`):

```scala
import longevity.model.annotations.component

@component
case class Email(email: String) {
  if (!email.contains('@'))
    throw new ConstraintValidationException("no '@' in email")
}
```

If you enforce constraints in this manner, then you will need to
provide your `LongevityContext` with some custom test data generators
to use the `RepoCrudSpec` or the `QuerySpec`. For instance, with the
above example, we could build a custom `Email` generator so that we
generate a nicely formed `Email` instead of just a random string:

```scala
import longevity.test.CustomGeneratorPool
import longevity.test.TestDataGenerator

val emailGenerator = { generator: TestDataGenerator =>
  Email(s"${generator.generate[String]}@${generator.generate[String]}")
}

val generators = CustomGeneratorPool.empty + emailGenerator
```

As shown above, you can recursively call the [test data
generator](http://longevityframework.org/scaladocs/emblem-latest/index.html#emblem.emblematic.traversors.sync.TestDataGenerator)
within your custom generator to construct your test data.

Pass in your custom generators when constructing your context like so:

```scala
val context = LongevityContext(
  domainModel,
  customGeneratorPool = generators)
```

The `customGeneratorPool` is an optional parameter that defaults to an
empty pool.

{% assign prevTitle = "generating test data" %}
{% assign prevLink = "test-data.html" %}
{% assign upTitle = "testing your domain model" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo crud spec" %}
{% assign nextLink = "repo-crud-spec.html" %}
{% include navigate.html %}

