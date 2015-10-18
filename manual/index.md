---
title: user manual
layout: page
---

Welcome to longevity! This user manual will show you all you need to
know in order to use it. We recommend you read through the manual
once, and then come back to it as a reference. If you are new to
Domain Driven Design, or feel that you might need a refresher, please
don't overlook the chapters on [the basics of DDD](./ddd-basics/).

{% capture content %}

From time to time in this manual, we introduce "longevity meta" notes
like this one, where we discuss the reasoning behind some of the
choices we made, rather than how to use longevity. We hope these notes
will help you to better understand why longevity is the way it is.

{% endcapture %}
{% include longevity-meta.html content=content%}

## Table of Contents

This is a rough outline of planned chapters, and subject to change. I
will put in links as I write the chapters.

- [What Is Longevity?](what-is-longevity.html)
- [The Basics of Domain Driven Design](ddd-basics/)
  - [Ubiquitous Language](ddd-basics/ubiquitous-language.html)
  - [Subdomains and Bounded Contexts](ddd-basics/subdomains-and-bounded-contexts.html)
  - [Aggregates and Entities](ddd-basics/aggregates-and-entities.html)
- [Project Setup](project-setup.html)
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

{% assign upTitle = "longevity site" %}
{% assign upLink = ".." %}
{% assign nextTitle="what is longevity" %}
{% assign nextLink="what-is-longevity.html" %}
{% include navigate.html %}
