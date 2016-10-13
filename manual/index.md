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
- [Persistent Types](persistent/ptypes.html)
- [Basic Properties](basics.html)
- [Collections](collections.html)
- [Embeddables](embeddable)
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
  - [Configuring your Longevity Context](context/config.html)
  - [Optimistic Locking](context/opt-lock.html)
  - [Repo Pools](context/repo-pools.html)
  - [Schema Creation](context/schema-creation.html)
  - [Persistent State](context/persistent-state.html)
  - [Persistent State Wrappers](context/pstate-wrappers.html)
- [Repositories](repo)
  - [The Repo API](repo/repo-api.html)
  - [Repo.create](repo/create.html)
  - [Creating Many Aggregates at Once](repo/create-many.html)
  - [Retrieval by Key Value](repo/retrieve.html)
  - [Repo.update](repo/update.html)
  - [Repo.delete](repo/delete.html)
  - [Polymorphic Repositories](repo/poly.html)  
- [Queries](query)
  - [Using the Query DSL](query/dsl.html)
  - [Query Filters](query/filters.html)
  - [Ordered Queries](query/order-by.html)
  - [Offsets and Limits](query/limit-offset.html)
  - [Retrieval by Query](query/retrieve-by.html)
  - [Stream by Query](query/stream-by.html)
  - [Limitations on Cassandra Queries](query/cassandra-query-limits.html)
- [Testing Your Subdomain](testing)
  - [In Memory Repositories](testing/in-mem-repos.html)
  - [Generating Test Data](testing/test-data.html)
  - [Enforcing Constraints](testing/constraints.html)
  - [RepoCrudSpec](testing/repo-crud-spec.html)
  - [QuerySpec](testing/query-spec.html)
- [Translating Persistents to the Database](translation)
  - [Persistent to JSON](translation/json.html)
  - [Partition Keys](translation/keys.html)
  - [MongoDB Translation](translation/mongo.html)
  - [Cassandra Translation](translation/cassandra.html)
- [Managing Logging](logging.html)

{% assign upTitle = "longevity site" %}
{% assign upLink = ".." %}
{% assign nextTitle="what is longevity" %}
{% assign nextLink="what-is-longevity.html" %}
{% include navigate.html %}
