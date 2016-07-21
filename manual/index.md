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
- [Embeddables](embeddable)
  - [Entities](embeddable/entities.html)
  - [Value Objects](embeddable/value-objects.html)
- [Key Values](key-values.html)
- [Limitations on Persistents, Embeddables, and Key Values](limitations.html)
- [The Persistent Type](ptype)
  - [Properties](ptype/properties.html)
  - [Keys](ptype/keys.html)
  - [Indexes](ptype/indexes.html)
  - [Prop Sets, Key Sets, and Index Sets](ptype/sets.html)
- [Polymorphic Embeddables](poly)
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
  - [Retrieval by Key Value](repo/retrieve.html)
  - [Retrieval by Query](repo/query.html)
  - [Stream by Query](repo/stream.html)
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
- [Translating Persistents to the Database](translation)
  - [Persistent to JSON](translation/json.html)
  - [Partition Keys](translation/keys.html)
  - [MongoDB Translation](translation/mongo.html)
  - [Cassandra Translation](translation/cassandra.html)

{% assign upTitle = "longevity site" %}
{% assign upLink = ".." %}
{% assign nextTitle="what is longevity" %}
{% assign nextLink="what-is-longevity.html" %}
{% include navigate.html %}
