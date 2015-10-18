---
title: subdomains and bounded contexts
layout: page
---

A domain is an enterprise-wide phenomenon, and can encompass a broad
range of concerns spanning multiple applications, data stores and
development teams. Constructing a single domain model for a business
can quickly become unweildy. For this reason, we break the domain into
subdomains.

A bounded context is that context where the language of a particular
subdomain is spoken. While a single application may speak in the
languages of more than one subdomain, ideally, their use is relegated to
separate, isolated modules. Information and event flow between
different bounded contexts occurs through context maps. Any number of
kinds of relationships can exist between two bounded contexts, and
Evans discusses them at length in his book. And rightly so, as
consistency between disparate applications tools and teams is critical
to any large enterprise.

In longevity, we work with a single subdomain at a time. You define
your subdomain, and longevity provides you with a `LongevityContext` -
that portion of your bounded context that is managed by longevity. For
now, longevity focuses on handling persistence concerns. In the
future, this could be expanded to include Rest APIs, domain events and
messaging, context maps, presentation layers (UI models), CQRS, and
event sourcing.

{% assign prevTitle = "ubiquitous language" %}
{% assign prevLink = "ubiquitous-language.html" %}
{% assign upTitle = "ddd basics" %}
{% assign upLink = "." %}
{% assign nextTitle="aggregates and entities" %}
{% assign nextLink="aggregates-and-entities.html" %}
{% include navigate.html %}

