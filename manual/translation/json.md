---
title: persistent to json
layout: page
---

As of now, all longevity [back ends](../context/pstrat.html) end up
translating persistent objects in and out of some JSON
variant. MongoDB uses BSON, whereas in Cassandra, the persistent
object is stored in JSON form in one of the columns. The translation
itself is quite straightforward:

- [Persistents](../persistent), [Embeddables](../embeddable), and
[KeyVals](../key-values.html) are translated into JSON Objects.
  - If an `Embeddable` or `KeyVal` being translated only has a single
member, that member value is inlined, so that the JSON produced is
whatever JSON the member value produces.
- [Basic values](../basics.html) translate directly into JSON
primitive types, with two exceptions:
  - `Chars` are translated into single-character strings.
  - In Mongo,
[DateTimes](http://www.joda.org/joda-time/apidocs/org/joda/time/DateTime.html)
are translated into a BSON
[ISODate](https://docs.mongodb.com/manual/reference/bson-types/#date).
  - In Cassandra, `DateTimes` are translated into a string in a
    lossless, time-zone preserving [ISO
    8061](https://en.wikipedia.org/wiki/ISO_8601) format.
- Empty `Options` are omitted from the JSON.
- Non-empty `Options` are inlined, so that `Some(expr)` will produce
the same JSON as `expr`.
- `Sets` and `Lists` are translated into JSON arrays.

We currently do not support the Cassandra native type `timestamp`
because it does not preserve time zone. It wouldn't be too hard to add
`timestamp` support as a configuration setting. We are more than happy
to provide you with assistance if you wanted to tackle implementing a
feature like this.

{% assign prevTitle = "translating persistents to the database" %}
{% assign prevLink = "." %}
{% assign upTitle = "translating persistents to the database" %}
{% assign upLink = "." %}
{% assign nextTitle = "mongodb translation" %}
{% assign nextLink = "mongo.html" %}
{% include navigate.html %}
