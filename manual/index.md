---
title: user manual
layout: page
---

Thanks for taking a look at longevity! This user manual will give you
all you need to know to use it. longevity is a [Domain Driven
Design](https://en.wikipedia.org/wiki/Domain-driven_design) framework,
and while you don't have to be an expert in DDD to use longevity, it
will help if you have some of the basics in mind. If DDD is new to
you, or you need a refresher, please read the three brief chapters on
DDD basics first.

I recommend you read through the manual once, and then come back to it
as a reference while you are using longevity.

## Table of Contents

This is a rough outline of planned chapters, and subject to change. I
will put in links as I write the chapters.

- [What Is Longevity?](./what-is-longevity.html)
- The Basics of Domain Driven Design
  - Ubiquitous Language
  - Subdomains and Bounded Contexts
  - Roots, Entities and Value Objects
- Project Setup
- Building Your Subdomain
  - Aggregate Roots
  - Natural Keys
  - Entities
  - Shorthands
  - Basics
  - Collections
  - Associations
  - Enforcing Constraints
- The Longevity Context
  - Using Your Repositories
    - Persistent State with `map` and `get`
    - CRUD Operations
    - Reactive with Futures
    - `Assoc.retrieve`
    - Optimistic Locking
  - Testing Your Subdomain
  - Configuring MongoDB
- Querying MongoDB Outside of Longevity
  - Translation of Aggregates into BSON
  - Translation of Persistent State into BSON
