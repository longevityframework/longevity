---
title: building the subdomain
layout: page
---

Once all the elements we want to persist have been created, we gather
them all together into a `Subdomain` object. We do this in
`SimblSubdomain`:

```scala
package simbl.domain

import longevity.subdomain.EType
import longevity.subdomain.ETypePool
import longevity.subdomain.PTypePool
import longevity.subdomain.Subdomain

class SimblSubdomain extends Subdomain(
  "Simple Blogging",
  PTypePool(User, Blog, BlogPost),
  ETypePool(
    EType[Markdown],
    EType[Uri],
    EType[UserProfile]))   
```

We need to gather up all our `PTypes` into a `PTypePool`. We also
create `ETypes` for our `Embeddables`, and gather them into an
`ETypePool`.  `PTypePool` and `ETypePool` are simple collections of
`PTypes` and `ETypes`. You can think of them as sets.

{% assign prevTitle = "username and email" %}
{% assign prevLink = "keyvals.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="building the longevity context" %}
{% assign nextLink="context.html" %}
{% include navigate.html %}
