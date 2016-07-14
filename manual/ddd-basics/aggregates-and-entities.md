---
title: aggregates and entities
layout: page
---

When we work with tools like [Hibernate](http://hibernate.org/) or
[Slick](http://slick.typesafe.com/) that, generally speaking, map a
row of a single relational table into a single Java or Scala object,
we end up thinking primarily in terms of entities. This is a bit of a
holdover from the days when we did our domain modeling in terms of
[entity-relationship
diagrams](https://en.wikipedia.org/wiki/Entity%E2%80%93relationship_model). As
the number of entities in our model grows, this can quickly
become a tangled knot of entities and relationships. In Domain Driven
Design, we shift our focus from the entity to the aggregate, in order
to bring a bit of order and organization to our domain model.

An aggregate is a small collection of domain entities, where one of
the entities in the collection is designated as the _aggregate
root_. Aggregates serve as a persistence unit, so that transactional
boundaries are drawn around them. All CRUD operations are performed
from the root, so no non-root entity requires a corresponding
repository. Aggregates also serve to simplify our entity-relationship
model by imposing the following rule:

- An entity is only allowed to have a relationship with a non-root
  entity if that entity is part of the same aggregate.

In essence, we are drawing a clear distinction between two different
kinds of relationships that occur in domain modelling: _composition_,
where one entity can be seen as _part of_ another entity, and
_aggregation_, where the two entities are much more loosely
associated. The hallmark of an aggregation is when the two entities
have independent life-cycles.

With Hibernate, all our relationships are represented by a direct
reference between the two entities. In longevity, we clearly
distinguish between the two kinds of relationships. Compositional
relationships are naturally modeled as nested case classes, and
aggregational relationships are modeled with using
[keys](../ptype/keys.html) and [key values](../key-values.html).

For a text-book example of DDD aggregates, please see this blog post
on [The Entity and the Aggregate
Root](http://scabl.blogspot.com/2015/03/aeddd-5.html)

{% assign prevTitle = "subdomains and bounded contexts" %}
{% assign prevLink = "subdomains-and-bounded-contexts.html" %}
{% assign upTitle = "ddd basics" %}
{% assign upLink = "." %}
{% assign nextTitle="project setup" %}
{% assign nextLink="../project-setup.html" %}
{% include navigate.html %}

