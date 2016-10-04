---
title: building your subdomain
layout: page
---

Your main task when working with longevity is constructing your
`Subdomain`. Once this is done, we provide you with persistence. Your
subdomain classes are the core of your application(s). The other
layers perform their individual functions while using the domain
classes fluidly.

Here are two ways to build an empty `Subdomain`:

```scala
import longevity.subdomain.Subdomain

// create your own domain type:

val subdomain = Subdomain("blogging")

// or put your subdomain in a companion object:

object BloggingDomain extends Subdomain("blogging")
```

{% assign prevTitle = "project setup" %}
{% assign prevLink = "project-setup.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = "." %}
{% assign nextTitle = "persistent objects" %}
{% assign nextLink = "persistent" %}
{% include navigate.html %}

