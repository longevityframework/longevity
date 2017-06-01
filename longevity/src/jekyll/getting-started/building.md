---
title: building the domain model
layout: page
---

Once all the elements we want to persist have been created, we gather
them all together into a `DomainModel` object. We do this in
`SimblDomainModel`:

```scala
package simbl.domain

import longevity.model.annotations.domainModel

@domainModel object SimblDomainModel
```

The `@domainModel` annotation uses package scanning to gather up all the
types we have labelled as `@persistent` and `@component`.

{% assign prevTitle = "username and email" %}
{% assign prevLink = "keyvals.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="building the longevity context" %}
{% assign nextLink="context.html" %}
{% include navigate.html %}
