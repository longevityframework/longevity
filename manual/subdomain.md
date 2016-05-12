---
title: building your subdomain
layout: page
---

Your main task when working with longevity is building your
`Subdomain`. Once this is done, we provide you with persistence. Your
subdomain classes are the core of you application(s). The other
layers perform their individual functions while using the domain
classes fluidly.

<div class="blue-side-bar">

As we mentioned earlier, we hope to cover more than just persistence
in the future. But we want to get the persistence part right, first.

</div>

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

val subdomain = Subdomain("blogging")

// you can also use these synonyms freely:
import longevity.subdomain.CoreDomain
import longevity.subdomain.SupportingSubdomain
import longevity.subdomain.GenericSubdomain

val coreDomain: CoreDomain = CoreDomain("blogging")
val supportingSubdomain: SupportingSubdomain = SupportingSubdomain("accounts")
val genericSubdomain: GenericSubdomain = GenericSubdomain("search")
```

{% assign prevTitle = "project setup" %}
{% assign prevLink = "project-setup.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = "." %}
{% assign nextTitle = "persistent objects" %}
{% assign nextLink = "persistent" %}
{% include navigate.html %}

