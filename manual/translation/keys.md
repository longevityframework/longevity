---
title: partition keys
layout: page
---

As of now, longevity maintains full control of the key used to
partition a table over multiple nodes.

For MongoDB, longevity generates an
[ObjectId](https://docs.mongodb.com/manual/reference/bson-types/#objectid)
and stores it in the [`_id`
field](https://docs.mongodb.com/manual/core/document/#document-id-field),
which acts as the [shard
key](https://docs.mongodb.com/manual/core/sharding-shard-key/#shard-key)
for the collection.

For Cassandra, longevity generates a
[UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier)
and stores it in a column named `id`, which serves as the table's
[primary
key](http://www.planetcassandra.org/blog/primary-keys-in-cql/).

It will probably disappoint some users and potential uses to hear that
they need to relinquish control of the partitioning of their
tables. Particularly Cassandra users, who want lightning fast behavior
for their [retrievals](../repo/retrieve.html). But don't be dismayed!
This is not a permanent state of affairs.

We plan to give users full control over the partition key in a future
story, found [here on our story
board](https://www.pivotaltracker.com/story/show/108382016). The
position of this story in our backlog (a few months out) is not an
indication of the importance we attribute to this story. It is more a
reflection of immediate need. We figure that users will not need this
feature for the first few months of project development, and that it
will be trivial to migrate an application to use this feature when it
is implemented. Consequently, this feature will gain a lot of
importance when adoption is higher. As always, please remember to [let
us know](https://groups.google.com/forum/#!forum/longevity-users) what
unimplemented features are important to you!

{% assign prevTitle = "persistent to json" %}
{% assign prevLink = "json.html" %}
{% assign upTitle = "translating persistents to the database" %}
{% assign upLink = "." %}
{% assign nextTitle = "mongodb translation" %}
{% assign nextLink = "mongo.html" %}
{% include navigate.html %}
