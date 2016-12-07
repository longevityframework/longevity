---
title: user manual
layout: page
---

Welcome to longevity! This user manual will show you all you need to
know in order to use it. We recommend you read through the manual
once, and then come back to it as a reference.

## Table of Contents

- [Preliminaries](prelims)
  - [What Is Longevity?](prelims/what-is-longevity.html)
  - [Project Setup](prelims/project-setup.html)
- [The Subdomain](subdomain)
  - [Persistent Objects](subdomain/persistents.html)
  - [Basic Values](subdomain/basics.html)
  - [Collections](subdomain/collections.html)
  - [Components](subdomain/components.html)
  - [Key Values](subdomain/key-values.html)
  - [Limitations on Persistents, Components, and Key Values](subdomain/limitations.html)
  - [Constructing a Subdomain](subdomain/subdomain.html)
- [The Persistent Type](ptype)
  - [Properties](ptype/properties.html)
  - [Keys](ptype/keys.html)
  - [Partition Keys](ptype/partition-keys.html)
  - [Indexes](ptype/indexes.html)
  - [Prop Sets, Key Sets, and Index Sets](ptype/sets.html)
- [Subtype Polymorphism](poly)
  - [Polymorphic Components](poly/components.html)
  - [Polymorphic Persistents](poly/persistents.html)
  - [Controlled Vocabularies](poly/cv.html)
- [Repositories](repo)
  - [Persistent State](repo/persistent-state.html)
  - [Persistent State Wrappers](repo/pstate-wrappers.html)
  - [The Repo API](repo/repo-api.html)
  - [Repo.create](repo/create.html)
  - [Repo.retrieve](repo/retrieve.html)
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
- [The Longevity Context](context)
  - [Configuring your Longevity Context](context/config.html)
  - [Optimistic Locking](context/opt-lock.html)
  - [Repo Pools](context/repo-pools.html)
  - [Schema Creation](context/schema-creation.html)
  - [Creating Many Aggregates at Once](context/create-many.html)
- [Testing Your Subdomain](testing)
  - [In Memory Repositories](testing/in-mem-repos.html)
  - [Generating Test Data](testing/test-data.html)
  - [Enforcing Constraints](testing/constraints.html)
  - [RepoCrudSpec](testing/repo-crud-spec.html)
  - [QuerySpec](testing/query-spec.html)
- [Translating Persistents to the Database](translation)
  - [Persistent to JSON](translation/json.html)
  - [MongoDB Translation](translation/mongo.html)
  - [MongoDB Keys](translation/mongo-keys.html)
  - [Cassandra Translation](translation/cassandra.html)
  - [Cassandra Keys](translation/cassandra-keys.html)
- [Managing Logging](logging.html)

{% assign upTitle = "longevity site" %}
{% assign upLink = ".." %}
{% assign nextTitle="what is longevity" %}
{% assign nextLink="what-is-longevity.html" %}
{% include navigate.html %}
