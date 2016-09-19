---
title: translating persistents to the database
layout: page
---

In this chapter, we discuss the details of how persistent objects are
stored in the database. This should aid you in interpreting the data
in your database, such as when using other tools to generate reports
from your database, or troubleshooting any problems that come up with
your application in production.

This treatment is broken down into the following sections:

- [Persistent to JSON](json.html)
- [Partition Keys](keys.html)
- [MongoDB Translation](mongo.html)
- [Cassandra Translation](cassandra.html)

{% assign prevTitle = "query spec" %}
{% assign prevLink = "../testing/query-spec.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "persistent to json" %}
{% assign nextLink = "json.html" %}
{% include navigate.html %}
