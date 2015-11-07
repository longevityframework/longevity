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
- [The Basics of Domain Driven Design](ddd-basics/)
  - [Ubiquitous Language](ddd-basics/ubiquitous-language.html)
  - [Subdomains and Bounded Contexts](ddd-basics/subdomains-and-bounded-contexts.html)
  - [Aggregates and Entities](ddd-basics/aggregates-and-entities.html)
- [Project Setup](project-setup.html)
- [Building Your Subdomain](subdomain)
  - [Kinds of Subdomains](subdomain/kinds.html)
  - [Aggregate Roots](subdomain/roots.html)
  - [Natural Keys](subdomain/keys.html)
  - [Basic Properties](subdomain/basics.html)
  - [Collections](subdomain/collections.html)
  - [Shorthands](subdomain/shorthands.html)
  - [Shorthand Pools](subdomain/shorthand-pools.html)
  - [Entities](subdomain/entities.html)
  - [Entities and Value Objects](subdomain/value-objects.html)
  - [Limitations on Entities and Shorthands](subdomain/limitations.html)
  - [Associations](subdomain/associations.html)
  - Where Not to Construct Your Subdomain
- The Longevity Context
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
