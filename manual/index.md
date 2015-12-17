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
- [Building Your Subdomain](subdomain)
  - [Kinds of Subdomains](subdomain/kinds.html)
  - [Aggregate Roots](subdomain/roots.html)
  - [Basic Properties](subdomain/basics.html)
  - [Collections](subdomain/collections.html)
- [Shorthands](shorthands)
  - [Shorthand Pools](shorthands/shorthand-pools.html)
  - [Where Not to Construct Your Shorthand Pools](shorthands/where-not.html)
- [Entities](entities)
  - [Entities and Value Objects](entities/value-objects.html)
  - [Limitations on Entities and Shorthands](entities/limitations.html)
- [Associations](associations)
  - [Using Associations](associations/using-associations.html)
- [The Root Type](root-type)
  - [Properties](root-type/properties.html)
  - [Keys](root-type/keys.html)
  - [Indexes](root-type/indexes.html)
- [The Longevity Context](context)
  - [Repo Pools](context/repo-pools.html)
  - Using Your Repositories
    - Persistent State with `map` and `get`
    - CRUD Operations
    - Reactive with Futures
    - `Assoc.retrieve`
    - Optimistic Locking
  - Something about Unpersisted Assocs
  - Testing Your Subdomain
  - Enforcing Constraints
  - Configuring your LongevityContext
- Querying MongoDB Outside of Longevity
  - Translation of Aggregates into BSON
  - Translation of Persistent State into BSON

{% assign upTitle = "longevity site" %}
{% assign upLink = ".." %}
{% assign nextTitle="what is longevity" %}
{% assign nextLink="what-is-longevity.html" %}
{% include navigate.html %}
