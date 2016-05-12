---
title: kinds of persistent objects
layout: page
---

The `Persistent` trait has three sub-traits that you can use in its
place: `Root`, `Event`, and `ViewItem`. These three traits are
designed to fill the roles of things we typically end up persisting
in Domain Driven Design applications.

Use `Root` as an aggregate root, when doing traditional DDD modelling
with [aggregates and
entities](../ddd-basics/aggregates-and-entities.html).

Use `Event` to persist events when doing [event-driven
systems](https://en.wikipedia.org/wiki/Event-driven_architecture), or
[event sourcing](http://martinfowler.com/eaaDev/EventSourcing.html).

Use `ViewItem` to store data to support queries, such as in a
[CQRS](http://martinfowler.com/bliki/CQRS.html) based system. Such
views are typically derivative of aggregates or events, reorganizing
the data for fast query response.

Of course, you can always use `Persistent` itself if what you want to
persist does not fit snugly into one of these three categories. Or you
can extend the trait yourself if you like.

At the moment, all four of these traits a functionally equivalent. We
tend to favor `Root` in our documentation, because it makes for clear
examples. But anything you can do with a `Root`, you can do with an
`Event`, a `ViewItem`, or a `Persistent`. In the future, we may
provide special support for aggregates, events, and/or views in the
longevity system. For now, we are focusing on getting persistence done
well.

{% assign prevTitle = "persistent objects" %}
{% assign prevLink = "." %}
{% assign upTitle = "persistent objects" %}
{% assign upLink = "." %}
{% assign nextTitle = "persistent types" %}
{% assign nextLink = "ptypes.html" %}
{% include navigate.html %}
