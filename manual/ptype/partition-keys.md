---
title: partition keys
layout: page
---

In order to accomodate performance in the face of very large data
sets, most NoSQL databases support [distributing data across multiple
nodes](https://en.wikipedia.org/wiki/Distributed_database). Distributed
databases commonly have a concept of a *partition key*, which we
define as a key for which, given a key value, we can determine the
node that the associated data lives on (or would live on, if it were
to exist). Because the database can determine the node up front, it
can route the query directly to the node that is able to satisfy the
query. For non-partitioned keys, every node has to be queried, and the
results aggregated.

As partition keys are a critical tool for working effectively with
NoSQL databases, longevity provides a variation on the keys discussed
in the [last section](keys.html) called a partition key. As various
NoSQL databases provide different mechanisms for defining and working
with partition keys, longevity needs to strike a balance between
providing full access to the power of the underlying database, and
providing an intuitive API for our users.

While we anticipate most users will be satisfied with the basic usage
of partition keys, which is quite simple and intuitive, a few
extensions are provided for more demanding users. As we describe those
advanced features here, we will briefly discuss how they relate to the
specific back ends that longevity supports. For a more complete
discussion on their implementation, please see the appropriate section
of the chapter on [translating persistents to the
database](../translation).

Let's take the `User` example from the previous chapter, and change
the `username` key into a partitioned key. All we need to do is change
the `key(props.username)` in our `keySet` to
`partitionKey(props.username)`:

```scala
import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@keyVal[User]
case class Username(username: String)

@persistent(keySet = Set(
  partitionKey(props.username)))
case class User(
  username: Username,
  firstName: String,
  lastName: String)
```

Now if our user table is distributed across 10 database nodes, a query
on the username key will only have to consult a single node, instead
of querying all 10 nodes and aggregating the results. (The partition
key query will actually hit two database nodes: the first database
node receives the query request, and routes the query to node that
holds the data.)

Once a persistent object is created, the key value for the partition
key cannot change. Longevity will reject any attempts to persist an
object with a modified partition key value by throwing a
`longevity.exceptions.persistence.UnstablePartitionKeyException`.

This is really all you need to know about the basic use case for
partition keys. It is of course up to you to configure your database
to be distributed across multiple nodes, but longevity will handle the
rest.

Longevity currently supports two advanced features for partition
keys. The first is using a *hashed key*, which you can employ as
follows:

```scala
partitionKey(props.username, hashed = true)
```

Hashed partition keys determine the appropriate database node based on
a [hash](https://en.wikipedia.org/wiki/Hash_function) of your key
value. This is in contrast to a *range key*, where the records are
kept in sorting order by key value. Range keys have the advantage of
supporting queries that return a range of key values, e.g., all the
usernames that start with an 's'. But unless the database is capable
of rebalancing your data, range keys suffer from an uneven
distribution of data.

All partition keys in Cassandra are hashed, and consequently, using
the `hashed` flag with a Cassandra back end has no effect. MongoDB
supports both hashed and ranged partition keys, and does automatically
rebalance your ranged keys. But take note that if you insert rows with
monotonically increasing ranged keys such as counters, you will still
[end up with a hot
spot](https://docs.mongodb.com/v3.2/core/sharding-shard-key/#shard-key-monotonic)
on the node that holds the far segment of the range.

The second advanced partition key feature that longevity supports is
partial partitions. In this case, the key value still determines the
node, but not every part of the key value is used to make that
determination. For example, we could define the `FullName` key from
the example in the [last section](keys.html) as a partition key, but
only partition on the last name. We would do this like so:

```scala
import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@keyVal[User]
case class Username(username: String)

@keyVal[User]
case class FullName(last: String, first: String)

@persistent(keySet = Set(
  key(props.username),
  partitionKey(props.fullName, partition(props.fullName.last))))
case class User(
  username: Username,
  fullName: FullName)
```

In this case, we can know that every user with the same last name will
be located on the same database node.

Because Cassandra does not support hashed keys, and MongoDB does not
support hashed keys in the face of partial partitions, longevity does
not currently support combining these two features.

{% assign prevTitle = "keys" %}
{% assign prevLink = "keys.html" %}
{% assign upTitle = "the persistent type" %}
{% assign upLink = "." %}
{% assign nextTitle = "indexes" %}
{% assign nextLink = "indexes.html" %}
{% include navigate.html %}

