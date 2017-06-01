---
title: the persistent type
layout: page
---

While the persistent objects are great places to store the information
we want to persist, we also need a place to keep track of "meta
information" about them. For instance, we want to be able to specify
_keys_, or properties of the object that uniquely identify a
persistent within a collection of persistents. We store this kind of
meta information in our `PTypes`.

It's important to note that, while "key" and "index" are terms used in
database schemas and physical modelling, that is not our intention
here. Within our `PTypes`, we are still describing the domain, not the
mapping of the domain into database schema. Of course, longevity will
[translate](../translation) these things into database indexes and
keys, and it does so in a consistent and predictable way, but that
does not mean that the uniqueness of a set of properties is not part
of the domain. The key question to ask ourselves in making such a
determination is, "would a domain expert care about such things"?
They would clearly care that an account number is unique, but they
_should not_ care about how that maps into a database schema.

Because we build our keys and indexes up out of properties, we will
discuss properties first.

- [Properties](properties.html)
- [Keys](keys.html)
- [Primary Keys](primary-keys.html)
- [Indexes](indexes.html)
- [Prop Sets, Key Sets, and Index Sets](sets.html)

{% assign prevTitle = "constructing a domain model" %}
{% assign prevLink  = "../model/model.html" %}
{% assign upTitle   = "user manual" %}
{% assign upLink    = ".." %}
{% assign nextTitle = "properties" %}
{% assign nextLink  = "properties.html" %}
{% include navigate.html %}

