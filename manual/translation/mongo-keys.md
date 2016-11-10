---
title: mongodb keys
layout: page
---

Longevity [indexes](../ptype/indexes.html) and non-partition
[keys](../ptype/keys.html) are translated directly into [MongoDB
indexes](https://docs.mongodb.com/manual/indexes/). Mongo indexes for
longevity keys will be
[unique](https://docs.mongodb.com/manual/core/index-unique/) when
there is no partition key.

If your `PType` does not define a [partition
key](../ptype/partition-keys.html), longevity generates an
[ObjectId](https://docs.mongodb.com/manual/reference/bson-types/#objectid)
and stores it in the [`_id`
field](https://docs.mongodb.com/manual/core/document/#document-id-field).
This `_id` column will be used for [update](../repo/update.html) and
[delete](../repo/delete.html) operations.

If you do define a partition key in your `PType`, longevity will still
store an `ObjectId` in the `_id` column, but it will never use it. It
will instead use your partition key for updates and deletes. MongoDB
requires that all stored documents have an `_id` column, even when you
specify your own sharding key.

Longevity generates an index for your partition key, and then uses
that index to shard the collection. If your database is not sharded,
the partition key will still be indexed, and function like a regular
key.

By default, the partition key index is a ranged index. If you specify
`hashed = true` when defining your partition key, then the index will
be hashed. Indexes for unhashed partition keys will be specified as
unique. Hashed partition keys will not, as MongoDB does not support
unique hashed indexes.

For partial partitions, longevity breaks down your partition key
property into the smallest set of constituent parts it needs to that
includes the properties in the partition. Any `retrieve` or
`retrieveOne` operation, and any [query filter](../query/filters.html)
or [order by clause](../query/order-by.html) that mentions the
partition key property, will break down the query along the lines of
these components parts.

While this breakdown of a partial partition key will be entirely
transparent to longevity users, and will perform perfectly well, it
may be a bit surprising to people who access their longevity
collections directly via MongoDB. But there is a valid reason for
it. When you shard a collection, you need to specify a prefix of the
properties in the index on which to shard. Even when every document in
the collection has `fullName` key to a sub-document that takes the
form `{ last: 'Smith', first: 'John' }`, MongoDB will not treat
`fullName.last` as a prefix of `fullName`. This is because MongoDB
does not know that the `fullName` key will always be a document that
starts with a `last` field. For this same reason, we need to break
down any queries on the partition key property in order to make use of
the underlying index.

Before moving on, we'd like to point out a handful of potential gotchas
with partition keys and MongoDB:

- MongoDB shard keys cannot exceed 512 bytes, so do not use a
  partition key if the key values might exceed this length.
- Avoid monotonically changing key values such as counters with non-hashed keys.
- Hashed partition keys will perform worse with range operations and
  ordered queries.
- Hashed partition keys will not enforce uniqueness.
- Using any kind of partition key will prevent MongoDB from enforcing
  uniqueness for any other key.

{% assign prevTitle = "mongodb translation" %}
{% assign prevLink = "mongodb.html" %}
{% assign upTitle = "translating persistents to the database" %}
{% assign upLink = "." %}
{% assign nextTitle = "cassandra translation" %}
{% assign nextLink = "cassandra.html" %}
{% include navigate.html %}
