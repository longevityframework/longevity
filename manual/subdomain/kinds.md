---
title: kinds of subdomains
layout: page
---

Domain Driven Design describes a few [different kinds of
subdomains](http://blog.jonathanoliver.com/ddd-strategic-design-core-supporting-and-generic-subdomains/),
such as _core_, _supporting_, and _generic_. In longevity, these are all
the same thing, but you can call them as you please. You can also make
use of multiple longevity `Subdomains` in a single application. (See
the [chapter on configuration](../context/config.html) for providing separate
configuration for each subdomain.)

Longevity `Subdomains` are quite easy to construct, and in this
section, we will review all the tools at your disposal to build
them. To get started, here are a few ways to build an empty
`Subdomain`:

```scala
import longevity.subdomain._

val subdomain = Subdomain("blogging", EntityTypePool.empty)

// you can also use these synonyms freely:
val coreDomain: CoreDomain = CoreDomain("blogging", EntityTypePool.empty)
val supportingSubdomain: SupportingSubdomain = SupportingSubdomain("accounts", EntityTypePool.empty)
val genericSubdomain: GenericSubdomain = GenericSubdomain("search", EntityTypePool.empty)
```

{% assign prevTitle = "building your subdomain" %}
{% assign prevLink = "." %}
{% assign upTitle = "building your subdomain" %}
{% assign upLink = "." %}
{% assign nextTitle = "aggregate roots" %}
{% assign nextLink = "roots.html" %}
{% include navigate.html %}

