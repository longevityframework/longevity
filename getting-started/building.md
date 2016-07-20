---
title: building the subdomain
layout: page
---

Once the elements of our
[subdomain](../manual/ddd-basics/subdomains-and-bounded-contexts.html)
have been created, we gather them all together into a `Subdomain`
object. We do this in `SimbleCoreDomain`:

```scala
package simbl.domain

import longevity.subdomain.CoreDomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.ptype.PTypePool

class SimblCoreDomain extends CoreDomain(
  "Simple Blogging",
  PTypePool(User, Blog, BlogPost),
  ETypePool(Markdown, Uri, UserProfile))
```

`SimbleCoreDomain` extends abstract class `CoreDomain`, which is a
kind of `Subdomain` that contains the core elements of your enterprise
domain.

We need to gather up all our `Persistents` and `Embeddables` into a
`PTypePool` and `ETypePool`, respectively. These are simple
collections of `PTypes` and `ETypes`. You can think of them as sets.

{% assign prevTitle = "username and email" %}
{% assign prevLink = "keyvals.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="building the longevity context" %}
{% assign nextLink="context.html" %}
{% include navigate.html %}
