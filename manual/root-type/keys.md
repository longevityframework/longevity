---
title: keys
layout: page
---

Keys are composed of a sequence of properties. We define them in our
`RootType` like so:

{% gist sullivan-/e2ef663857157a03a301 %}

The values of the properties in a key uniquely identify a row. So in
the above example, no two users can have the same username.

We can declare multiple keys, and composite keys, just as
easily. Here, for instance, we add an ill-advised composite key on a
`firstName`/`lastName` combination:

{% gist sullivan-/b72900a6882b557e6728 %}

Here, no two users can have the same first and last names.

We use keys to retrieve individual entity aggregates from the
persistence layer, as we will see in the [chapter on
`Repo.retrieve`](../repo/retrieve-keyval.html). You are most likely
going to want to define at least one per aggregate, or you will only
be able to retrieve collections of aggregates by query. It is possible
that you have an entity type - perhaps representing an entry in a log
file - for which there are no natural keys. You may be satisfied to
confine yourself to looking up collections of these entities via
queries such as range searches.

It's worth reiterating that the keys that we define in our domain
model are not database keys, but design constraints that live within
the domain model itself. That being said, they do map to database keys
in predictable ways. See the chapters on [translation into
MongoDB](../mongo) and [translation into Cassandra](../cassandra).

{% assign prevTitle = "properties" %}
{% assign prevLink = "properties.html" %}
{% assign upTitle = "the root type" %}
{% assign upLink = "." %}
{% assign nextTitle = "indexes" %}
{% assign nextLink = "indexes.html" %}
{% include navigate.html %}

