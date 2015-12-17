---
title: the root type
layout: page
---

While the `Root` and `Entity` classes are great places to store
information about the individual roots and entities themselves, we
also need a place to keep track of "meta information" about our
aggregates. For instance, we want to be able to specify _keys_, or
collections of properties of the aggregate that uniquely identifies an
aggregate among a collection of aggregates. We define these things in
our `RootTypes`.

It's important to note that, while "key" and "index" are terms used in
database schemas and physical modelling, that is not our intention
here. Within our `RootTypes`, we are still describing the domain, not
the mapping of the domain into database schema. Of course, longevity
will translate these things into database indexes and keys, and it
does so in a consistent and predictable way, but that does not mean
that the uniqueness of a set of properties is not part of the
domain. The key question to ask ourselves in making such a
determination is, "would a domain expert care about such things"?
They would clearly care that an account number is unique, but they
_should not_ care about how that maps into a database schema.

As we build our keys and indexes up out of properties, we discuss
properties first.

- [Properties](properties.html)
- [Keys](keys.html)
- [Indexes](indexes.html)

{% assign prevTitle = "using associations" %}
{% assign prevLink = "../subdomain/using-associations.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "properties" %}
{% assign nextLink = "properties.html" %}
{% include navigate.html %}

