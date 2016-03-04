---
title: persistence strategy
layout: page
---

The persistence strategy describes the kind of back-end you would like
to work with. At present, there are three strategies that you can
select:

- `longevity.context.InMem`
- `longevity.context.Mongo`
- `longevity.context.Cassandra`

You specify your persistence strategy when you construct your
`LongevityContext`:

    import longevity.context.LongevityContext
    import longevity.context.Cassandra
    
    val context = LongevityContext(subdomain, Cassandra)

The persistence strategy currently defaults to `Mongo`. We should
probably change that to not have a default.

{% assign prevTitle = "the longevity context" %}
{% assign prevLink = "." %}
{% assign upTitle = "the longevity context" %}
{% assign upLink = "." %}
{% assign nextTitle = "configuring your longevity context" %}
{% assign nextLink = "config.html" %}
{% include navigate.html %}

