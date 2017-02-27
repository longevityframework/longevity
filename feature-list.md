---
title: feature list
layout: page
---

Current features are described in detail in the [user
manual](manual). They include:

- Asynchronous persistence API using [Scala
  futures](http://docs.scala-lang.org/overviews/core/futures.html) and
  [Akka
  streams](http://doc.akka.io/docs/akka/current/scala/stream/index.html).
- Cassandra, MongoDB, SQLite, and in-memory back ends.

- Supports flexible construction of your domain model using standard
  Scala types such as case classes, options, sets and lists.
- Compile-time DSL to express keys, queries, and indexes.
- User control of [sharding](https://docs.mongodb.com/manual/sharding/#shard-keys)/[partitioning](https://docs.datastax.com/en/cql/3.1/cql/cql_reference/refCompositePk.html).
- Polymorphic traits and controlled vocabularies.
- Boilerplate-free description of your domain model.

- Pre-built repositories with simple, reactive APIs.
- Configuration-level optimistic locking.
- Pre-built integration tests that exercise your repositories
  against a real database.
- Fully featured in-memory repositories for use in other integration
  testing.
- Test data generation.

## Upcoming Features

The current high-priority features are:

- Support more streaming libraries for queries.
- Free monad wrapper around the repository API.
- Schema migration framework.
- First-class support for optional values.

For more information, please see the [story
board](https://www.pivotaltracker.com/n/projects/1231978). Tickets are
roughly ordered in order of current priority.
