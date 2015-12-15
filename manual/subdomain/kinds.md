---
title: kinds of subdomains
layout: page
---

Domain Driven Design describes a few [different kinds of
subdomains](http://blog.jonathanoliver.com/ddd-strategic-design-core-supporting-and-generic-subdomains/),
such as _core_, _supporting_, and _generic_. In longevity, these are all
the same thing, but you can call them as you please. You can also make
use of multiple longevity `Subdomains` in a single application.

TODO: link here to longevity config chapter on multiple contexts.

Longevity `Subdomains` are quite easy to construct, and in this
section, we will review all the tools at your disposal to build
them. To get started, here are a few ways to build an empty
`Subdomain`:

{% gist sullivan-/1bf6e826ce266588ecde %}

{% assign prevTitle = "building your subdomain" %}
{% assign prevLink = "." %}
{% assign upTitle = "building your subdomain" %}
{% assign upLink = "." %}
{% assign nextTitle = "aggregate roots" %}
{% assign nextLink = "roots.html" %}
{% include navigate.html %}

