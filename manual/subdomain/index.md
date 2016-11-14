---
title: the subdomain
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

// put your subdomain in a local val:

val subdomain = Subdomain("blogging")

// or define your subdomain as a singleton object:

object BloggingDomain extends Subdomain("blogging")
```

Of course, we don't really want an empty subdomain. In the rest of
this chapter, we will cover the basics of filling your subdomain with
the classes you want to persist:

- [Persistent Objects](persistents.html)
- [Persistent Types](ptypes.html)
- [Basic Properties](basics.html)
- [Collections](collections.html)
- [Components](components.html)
- [Key Values](key-values.html)
- [Limitations on Persistents, Embeddables, and Key Values](limitations.html)

{% assign prevTitle = "project setup" %}
{% assign prevLink  = "../prelims/project-setup.html" %}
{% assign upTitle   = "user manual" %}
{% assign upLink    = ".." %}
{% assign nextTitle = "persistent objects" %}
{% assign nextLink  = "persistents.html" %}
{% include navigate.html %}

