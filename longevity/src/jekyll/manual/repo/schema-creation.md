---
title: schema creation
layout: page
---

Most back ends will need to perform some sort of schema creation
routine before the repositories are ready to use. The behavior of
using the repositories without schema creation will vary dramatically,
depending on the back end. The cassandra back end will not run at
all. MongoDB is more forgiving, and the repositories will function
without any schema creation, but performance will suffer due to the
lack of indexes. (You will also fail to get any exceptions due to
duplicate key values, as discussed in the [section on method
`Repo.create`](../repo/create.html).) For the in-memory back end,
schema creation is a no-op.

You can create the requisite schema by calling `Repo.createSchema()`. This is an asynchronous
method, so you will need to provide an implicit `ExecutionContext`, and you will want to ensure the
asynchronous method completes before continuing. It returns a `Future[Unit]` - that is, the
completion of the future will only indicate success or failure.

An alternative way to generate schema is to set the [configuration flag](../context/config.html)
`longevity.autoCreateSchema` to true. In this case, schema will be generated when the [connection is
opened](connection.html). Schema autogeneration is a convenient feature for testing or development
work, but will incur an overhead for every program run.

Schema generation is always a non-destructive process, and you will
never lose any data when generating schema.

{% assign prevTitle = "opening and closing the connection" %}
{% assign prevLink  = "connection.html" %}
{% assign upTitle   = "the repository" %}
{% assign upLink    = "." %}
{% assign nextTitle = "the persistent state" %}
{% assign nextLink  = "persistent-state.html" %}
{% include navigate.html %}
