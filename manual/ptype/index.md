---
title: the persistent type
layout: page
---

While the `Persistent` and `Entity` classes are great places to store
information about the individual entitites and entities themselves, we
also need a place to keep track of "meta information" about our
aggregates. For instance, we want to be able to specify _keys_, or
collections of properties of the aggregate that uniquely identifies an
aggregate among a collection of aggregates. We define these things in
our `PTypes`.

It's important to note that, while "key" and "index" are terms used in
database schemas and physical modelling, that is not our intention
here. Within our `PTypes`, we are still describing the domain, not
the mapping of the domain into database schema. Of course, longevity
will translate these things into database indexes and keys, and it
does so in a consistent and predictable way, but that does not mean
that the uniqueness of a set of properties is not part of the
domain. The key question to ask ourselves in making such a
determination is, "would a domain expert care about such things"?
They would clearly care that an account number is unique, but they
_should not_ care about how that maps into a database schema.

The `PType` trait has child traits that match the `Persistent`
hierarchy. `RootType` corresponds to `Root`, `EventType` corresponds
to `Event`, and `View` corresponds to `ViewItem`. At present, they all
have the same behavior.

Because we build our keys and indexes up out of properties, we will
discuss properties first.

- [Properties](properties.html)
- [Keys](keys.html)
- [Indexes](indexes.html)
- [Key Sets and Index Sets](key-sets-and-index-sets.html)

{% assign prevTitle = "using associations" %}
{% assign prevLink = "../subdomain/using-associations.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "properties" %}
{% assign nextLink = "properties.html" %}
{% include navigate.html %}

