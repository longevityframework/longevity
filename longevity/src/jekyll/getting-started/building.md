---
title: declaring the domain model
layout: page
---

Before we start building out the elements of our domain, we want to declare the domain model as a
type. We call it `SimblDomainModel`, and decorate it with a `@domainModel` annotation:

```scala
package simbl.domain

import longevity.model.annotations.domainModel

@domainModel trait SimblDomainModel
```

The `@domainModel` annotation does not do anything to our `SimblDomainModel` trait itself. Instead,
it inserts some useful behind-the-scenes information into the `SimblDomainModel` companion object.
The [user manual](../manual) has all the details of what goes on behind the scenes.

{% assign prevTitle = "modelling our domain" %}
{% assign prevLink  = "modelling.html" %}
{% assign upTitle   = "getting started guide" %}
{% assign upLink    = "." %}
{% assign nextTitle = "building the user aggregate" %}
{% assign nextLink  = "user.html" %}
{% include navigate.html %}
