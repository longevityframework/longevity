---
title: building your subdomain
layout: page
---

Your main task when working with longevity is building your
`Subdomain`. Once this is done, we provide you with persistence. Your
subdomain classes are the core of you application(s). The other
layers perform their individual functions while using the domain
classes fluidly.

{% capture content %}

As we mentioned earlier, we hope to cover more than just persistence
in the future. But we want to get the persistence part right, first.

{% endcapture %}
{% include longevity-meta.html content=content%}

Our discussion on building subdomains breaks down as follows:

- [Kinds of Subdomains](kinds.html)
- [Aggregate Roots](roots.html)
- [Natural Keys](keys.html)
- [Basic Properties](basics.html)
- [Collections](collections.html)
- Shorthands
- Entities
- Associations
- Enforcing Constraints

{% assign prevTitle = "project setup" %}
{% assign prevLink = "../project-setup.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle="kinds of subdomains" %}
{% assign nextLink="kinds.html" %}
{% include navigate.html %}

