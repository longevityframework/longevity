---
title: user manual
layout: page
---

Welcome to longevity! This user manual will show you all you need to
know in order to use it. We recommend you read through the manual
once, and then come back to it as a reference. If you are new to
Domain Driven Design, or feel that you might need a refresher, please
don't overlook the chapters on [the basics of DDD](./ddd-basics/).

## Table of Contents

This is a rough outline of planned chapters, and subject to change. I
will put in links as I write the chapters.

- [What Is Longevity?](what-is-longevity.html)
- [The Basics of Domain Driven Design](ddd-basics)
  - [Ubiquitous Language](ddd-basics/ubiquitous-language.html)
  - [Subdomains and Bounded Contexts](ddd-basics/subdomains-and-bounded-contexts.html)
  - [Aggregates and Entities](ddd-basics/aggregates-and-entities.html)
- [Project Setup](project-setup.html)
- [Building Your Subdomain](subdomain.html)
- [Persistent Objects](persistent)
  - [Kinds of Persistent Objects](persistent/kinds.html)
  - [Persistent Types](persistent/ptypes.html)
- [Basic Properties](basics.html)
- [Collections](collections.html)
- [Shorthands](shorthands)
  - [Shorthand Pools](shorthands/shorthand-pools.html)
- [Entities](entities)
  - [Entities and Value Objects](entities/value-objects.html)
- [Limitations on Persistents, Entities and Shorthands](limitations.html)
- [Associations](associations)
  - [Using Associations](associations/using-associations.html)
- [The Persistent Type](ptype)
  - [Properties](ptype/properties.html)
  - [Keys](ptype/keys.html)
  - [Indexes](ptype/indexes.html)
  - [Key Sets and Index Sets](ptype/key-sets-and-index-sets.html)
- [Polymorphic Persistents and Entities](poly)
  - [Polymorphic Persistents](poly/persistents.html)
- [The Longevity Context](context)
  - [Persistence Strategy](context/pstrat.html)
  - [Configuring your Longevity Context](context/config.html)
  - [Repo Pools](context/repo-pools.html)
  - [Persistent State](context/persistent-state.html)
- [Repositories](repo/index.html)
  - [The Repo API](repo/repo-api.html)
  - [Repo.create](repo/create.html)
  - [Creating Many Aggregates at Once](repo/create-many.html)
  - [Retrieval by Assoc](repo/retrieve-assoc.html)
  - [Retrieval by Key Value](repo/retrieve-keyval.html)
  - [Retrieval by Query](repo/query.html)
  - [Limitations on Cassandra Queries](repo/cassandra-query-limits.html)
  - [Repo.update](repo/update.html)
  - [Repo.delete](repo/delete.html)
  - [Polymorphic Repositories](repo/poly.html)
- FPState and FOPState (TODO)
- [Testing Your Subdomain](testing)
  - [In Memory Repositories](testing/in-mem-repos.html)
  - [RepoCrudSpec](testing/repo-crud-spec.html)
  - [QuerySpec](testing/query-spec.html)
- [Enforcing Constraints](constraints.html)
- [Translation into MongoDB](mongo) (TODO)
  - Translation of Aggregates into BSON (TODO)
  - Translation of Persistent State into BSON (TODO)
  - Keys and Indexes (TODO)
- [Translation into Cassandra](cassandra) (TODO)

{% assign upTitle = "longevity site" %}
{% assign upLink = ".." %}
{% assign nextTitle="what is longevity" %}
{% assign nextLink="what-is-longevity.html" %}
{% include navigate.html %}
