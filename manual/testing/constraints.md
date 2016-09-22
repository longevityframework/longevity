---
title: enforcing constraints
layout: page
---

The test data generator described in the [previous
section](test-data.html) will handle most anything you put in your
domain. The only thing we cannot handle out of the box is when
exceptions are thrown in the constructors of your
[persistent](persistent) and [embeddable](embeddable) classes.

A class constructor is a great place to enforce domain constraints,
such as requiring that an email has an at sign (`@`):

```scala
case class Email(email: String) extends ValueObject {
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
import emblem.traversors.sync.CustomGeneratorPool
import emblem.traversors.sync.CustomGenerator

val emailGenerator = CustomGenerator.simpleGenerator[Email] {
  generator => Email(s"{generator.string}@{generator.string")
}
val generators = CustomGeneratorPool.empty + emailGenerator
```

In nearly all cases, you can get away with building a [simple
generator](http://longevityframework.github.io/longevity/scaladocs/emblem-latest/index.html#emblem.emblematic.traversors.sync.CustomGenerator$@simpleGenerator[A](underlying:emblem.emblematic.traversors.sync.Generator=>A)(implicitevidence$2:emblem.imports.TypeKey[A]):emblem.emblematictraversors.sync.CustomGenerator[A]),
as above. You can recursively call the
[test data generator](http://longevityframework.github.io/longevity/scaladocs/emblem-latest/index.html#emblem.emblematic.traversors.sync.TestDataGenerator)
within your custom generator to construct your test data.

You just pass in your custom generators when constructing your
context:

```scala
val cassandraContext = LongevityContext(
  subdomain,
  customGeneratorPool = generators)
```

The `customGeneratorPool` is an optional parameter that defaults to an
empty set.

{% assign prevTitle = "generating test data" %}
{% assign prevLink = "test-data.html" %}
{% assign upTitle = "testing your subdomain" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo crud spec" %}
{% assign nextLink = "repo-crud-spec.html" %}
{% include navigate.html %}

