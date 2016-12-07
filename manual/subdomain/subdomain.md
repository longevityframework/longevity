---
title: constructing a subdomain
layout: page
---

Now that we've built out the elements of our domain model, we need to
collect them into a `longevity.subdomain.Subdomain` object. The
easiest way to do this is as follows:

```scala
package myPackage

import longevity.subdomain.annotations.subdomain

@subdomain
object mySubdomain
```

The `@subdomain` annotation scans the current package, recursively
scanning any sub-packages, to gather your [persistent
objects](persistents.html) and [components](components.html).

For the non-annotation equivalent, you need to manually specify the
package name:

```scala
package myPackage

import longevity.subdomain.Subdomain

object mySubdomain extends Subdomain("myPackage")
```

The package scanning is a one-time cost and relatively cheap, but if
you would prefer to avoid it, you can enumerate your `PTypes` and
`CTypes` by hand, like so:

```scala
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool

object mySubdomain extends Subdomain(
  PTypePool(User, BlogPost, Blog),
  CTypePool(UserProfile))
```

{% assign prevTitle = "limitations on persistents, components, and key values" %}
{% assign prevLink  = "limitations.html" %}
{% assign upTitle   = "the subdomain" %}
{% assign upLink    = "." %}
{% assign nextTitle = "the persistent type" %}
{% assign nextLink  = "../ptype" %}
{% include navigate.html %}

