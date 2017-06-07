---
title: write timestamps
layout: page
---

Setting the configuration variable `longevity.writeTimestamps` to
`true` will cause longevity to store two timestamp values for each
persistent object: a timestamp for when it was created, and a
timestamp for when it was last modified. These values are not
accessible through longevity. Instead, they are provided for
diagnostic purposes when examining the contents of your database
directly.

In MongoDB, these values are stored in document properties
`_createdTimestamp` and `_updatedTimestamp`. In Cassandra and SQLite,
they are stored in columns `created_timestamp` and
`updated_timestamp`.

The behavior of turning the `longevity.writeTimestamps` configuration
flag on and off varies slightly, depending on your back end. If a
persistent object is created when this flag is off, the object will
never have a created timestamp. If the flag was previously on, but
turned off, then in MongoDB, subsequent writes will remove the old
diagnostic values. In Cassandra and SQLite, subsequent writes will
leave the two timestamps unchanged. This is because MongoDB
typically rewrites the whole document on update. With Cassandra and
SQLite, the two columns cannot be referenced when the flag is off, as
longevity cannot know that the columns even exist.

If you decide to turn this flag on with an existing database, you will
probably want to rerun `RepoPool.createSchema()`, to make sure that
any schema to support the two timestamps (i.e., table columns for
Cassandra and SQLite) is in place.

{% assign prevTitle = "optimistic locking" %}
{% assign prevLink  = "opt-lock.html" %}
{% assign upTitle   = "the longevity context" %}
{% assign upLink    = "." %}
{% assign nextTitle = "testing your domain model" %}
{% assign nextLink  = "../testing" %}
{% include navigate.html %}

