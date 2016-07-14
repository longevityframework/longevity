---
title: ubiquitous language
layout: page
---

Domain Driven Design places a strong emphasis on the language used by
the team in all stages of development. When modelling our system, we
have conversations with the domain experts to better understand the
domain. In these conversations, language can often be vague, with some
words used interchangeably, and some words used to express more than
one concept. To come to a clear and common understanding of our
domain, we root out the ambiquities of our language, and relegate each
term to have a precise meaning.

Because of the importance of understanding our domain, and the
importance of the role that language plays in this process, we agree
to use the language of the domain in all contexts of the
project. Discussions with domain experts, among developers, the
language used in documents, and especially in the code itself, should
all conform to the same vocabulary. Thus, the language of our domain
becomes ubiquitous, or "used everywhere".

Ideally, the coding elements that model our domain would be a direct
translation of the elements we might include in a UML modelling
diagram of the same domain. If we can accomplish this, then the domain
classes can serve as a source of truth for the domain itself. And the
code that uses the domain classes can more clearly focus on domain
concerns. But we are often hampered in meeting this ideal, as our
domain classes are infected with persistence concerns. Even the
typical clutter of a Java POJO - getters and setters for the fields,
complex overrides for `hashCode` and `equals` - get in the way of
our domain classes serving as a reference for the model.

In longevity, the use of Scala case classes, and the careful
encapsulation of persistence concerns, allows us to write domain
classes that directly reflect our domain, as we will see in the
section on [building your subdomain](../subdomain.html).

{% assign prevTitle = "ddd basics" %}
{% assign prevLink = "." %}
{% assign upTitle = "ddd basics" %}
{% assign upLink = "." %}
{% assign nextTitle="subdomains and bounded contexts" %}
{% assign nextLink="subdomains-and-bounded-contexts.html" %}
{% include navigate.html %}

