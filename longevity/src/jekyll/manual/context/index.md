---
title: the longevity context
layout: page
---

In broad terms, you build your `LongevityContext` by specifying your domain model as a type
parameter, and optionally providing a configuration. Without specifying a configuration, it looks
like this:

```scala
import longevity.context.LongevityContext

val context = LongevityContext[DomainModel]()
```

This calls take an implicit parameter of type `longevity.model.ModelEv[DomainModel]`. Because we
annotated our `DomainModel` trait as a longevity domain model, like so:

```scala
import longevity.model.annotations.domainModel

@domainModel trait DomainModel
```

The `ModelEv[DomainModel]` is found by the Scala compiler in the `DomainModel` companion object.

The longevity context contains a variety of tools for you to use relating to your model. The main
thing it gives you is the [repository](../repo) - which supplies all the basic persistence
operations you need to maintain your back-end store. But there are many other tools there, most of
which are most useful when [writing tests](../testing).

- [Configuring your Longevity Context](config.html)
- [Repositories in the Context](repos.html)
- [Optimistic Locking](opt-lock.html)
- [Write Timestamps](write-timestamps.html)

{% assign prevTitle = "controlled vocabularies" %}
{% assign prevLink  = "../poly/cv.html" %}
{% assign upTitle   = "user manual" %}
{% assign upLink    = ".." %}
{% assign nextTitle = "configuring your longevity context" %}
{% assign nextLink  = "config.html" %}
{% include navigate.html %}

