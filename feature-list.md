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

- Supports construction of your domain model using:
  - Basic values such as `Int`, `String`, `DateTime`, etc.
  - Collection types such as `Option`, `Set`, and `List`.
  - Case classes.
  - Polymorphic traits.
  - Controlled vocabularies.
- Domain-level keys and indexes.
- User control of [sharding](https://docs.mongodb.com/manual/sharding/#shard-keys)/[partitioning](https://docs.datastax.com/en/cql/3.1/cql/cql_reference/refCompositePk.html).
- [Macro
  annotations](http://docs.scala-lang.org/overviews/macros/annotations.html)
  and package scanning to remove all boilerplate in describing your
  domain model.

- Pre-built repositories with simple, reactive APIs.
- Configuration-level optimistic locking.
- Query DSL for retrieval or streaming of multiple records.
- Pre-built integration tests that exercise your repositories
  against a real database.
- Fully featured in-memory repositories for use in other integration
  testing.
- Test data generation.
- JSON marshallers.

## Upcoming Features

Known, upcoming features are tracked on the [story
board](https://www.pivotaltracker.com/n/projects/1231978). You will
find scheduled upcoming features in the "Current" and "Backlog"
pages. Other future features can be found in the "Icebox", which is
roughly ordered by current priorities.
