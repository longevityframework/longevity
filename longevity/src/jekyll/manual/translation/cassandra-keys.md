---
title: cassandra keys
layout: page
---

If your `PType` does not define a [primary
key](../ptype/primary-keys.html), longevity generates a
[UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier)
and stores it in a column named `id`, which serves as the Cassandra
table's [primary
key](https://docs.datastax.com/en/cql/3.3/cql/cql_using/useSimplePrimaryKeyConcept.html). This
column will be used for [update](../repo/update.html) and
[delete](../repo/delete.html) operations.

If you do define a primary key, longevity will use the property
columns described in the [previous section](cassandra.html) to build
your Cassandra primary key. If you do not specify a partial partition,
then all of the columns will become part of a [Cassandra composite
partition
key](https://docs.datastax.com/en/cql/3.3/cql/cql_using/useCompositePartitionKeyConcept.html).
If you do specify a partial partition, then those columns that make up
your partial partition will become the Cassandra partition key, and
the remaining columns will make up the remainder of your primary key.
Longevity will use these columns for updates and deletes.

In Cassandra, the partition key is always hashed. Any remaining
columns in a partially partitioned primary key will be ranged,
ascending. (While Cassandra supports these columns being
ranged in *descending* order, we do not currently pass on that
functionality to the longevity user.)

Please be aware that Cassandra will not reject
[create](../repo/create.html) operations for objects that have the
same primary key as an existing object. Rather, it will simply
overwrite these rows.

{% assign prevTitle = "cassandra translation" %}
{% assign prevLink  = "cassandra.html" %}
{% assign upTitle   = "translating persistents to the database" %}
{% assign upLink    = "." %}
{% assign nextTitle = "mongodb translation" %}
{% assign nextLink  = "mongo.html" %}
{% include navigate.html %}
