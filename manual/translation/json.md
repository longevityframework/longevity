---
title: persistent to json
layout: page
---

As of now, all longevity database back ends translate persistent
objects in and out of some JSON variant. MongoDB uses BSON, whereas in
Cassandra, the persistent object is stored in JSON form in one of the
columns. The translation itself is quite straightforward:

- [Persistent objects](../subdomain/persistents.html), [persistent
components](../subdomain/components.html), and
[key values](../subdomain/key-values.html) are translated into JSON
Objects.
- If a persistent component or a `KeyVal` being translated only has a
single member, that member value is inlined, so that the JSON produced
is whatever JSON the member value produces.
- [Basic values](../basics.html) translate directly into JSON
primitive types, with two exceptions:
  - `Chars` are translated into single-character strings.
  - [DateTimes](http://www.joda.org/joda-time/apidocs/org/joda/time/DateTime.html)
    are converted to UTC time zone before serializing to
    avoid ordering issues.
    - In (non-BSON) JSON, they are converted into a string in a
      lossless [ISO 8061](https://en.wikipedia.org/wiki/ISO_8601) format.
    - In Mongo, `DateTimes` are translated into a BSON
      [ISODate](https://docs.mongodb.com/manual/reference/bson-types/#date).
    - In Cassandra, when `DateTimes` are stored in individual columns,
      they are stored as
      [timestamps](https://docs.datastax.com/en/cql/3.1/cql/cql_reference/timestamp_type_r.html) 
- Empty `Options` are omitted from the JSON.
- Non-empty `Options` are inlined, so that `Some(expr)` will produce
the same JSON as `expr`.
- `Sets` and `Lists` are translated into JSON arrays.

{% assign prevTitle = "translating persistents to the database" %}
{% assign prevLink = "." %}
{% assign upTitle = "translating persistents to the database" %}
{% assign upLink = "." %}
{% assign nextTitle = "mongodb translation" %}
{% assign nextLink = "mongo.html" %}
{% include navigate.html %}
