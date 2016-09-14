---
title: user manual
layout: page
---

Welcome to longevity! This user manual will show you all you need to
know in order to use it. We recommend you read through the manual
once, and then come back to it as a reference.

## Table of Contents

- [What Is Longevity?](what-is-longevity.html)
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
- [Subtype Polymorphism](poly)
  - [Polymorphic Embeddables](poly/embeddables.html)
  - [Polymorphic Persistents](poly/persistents.html)
  - [Controlled Vocabularies](poly/cv.html)
- [The Longevity Context](context)
  - [Persistence Strategy](context/pstrat.html)
  - [Configuring your Longevity Context](context/config.html)
  - [Optimistic Locking](context/opt-lock.html)
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
- [A Primer on Domain Driven Design](ddd-basics)
  - [Ubiquitous Language](ddd-basics/ubiquitous-language.html)
  - [Subdomains and Bounded Contexts](ddd-basics/subdomains-and-bounded-contexts.html)
  - [Aggregates and Entities](ddd-basics/aggregates-and-entities.html)

{% assign upTitle = "longevity site" %}
{% assign upLink = ".." %}
{% assign nextTitle="what is longevity" %}
{% assign nextLink="what-is-longevity.html" %}
{% include navigate.html %}
