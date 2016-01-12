---
title: retrieval by query
layout: page
---

You can use queries to retrieve zero or more aggregates of a given
type. For instance, looking up all the blog posts for a blog published
in the last week:

{% gist sullivan-/fa2001b32ea19084a3d0 %}

<div class="blue-side-bar">

Of course, we would <a href =
"https://www.pivotaltracker.com/story/show/109042398">prefer to
return a stream here</a>, rather than a sequence wrapped in a future.

</div>

Anything you can do with the `Query` factory methods, as shown above,
you can do using the Query DSL instead:

{% gist sullivan-/42caecbb5b2096afd4a8 %}

If you don't want the DSL wildcard imports to infect other parts of
your program, it is quite easy to localize them:

{% gist sullivan-/ca39baf6637037529421 %}

The query syntax is currently quite limited, and is a [focal point of
future
work](https://www.pivotaltracker.com/epic/show/2253386). Currently,
the following query keywords are supported:

  - `and`, `or`, `eqs`, `neq`, `lt`, `lte`, `gt`, `gte`

The six comparator operators all take a
[property](../root-type/properties.html) on the left-hand side, and a
value on the right-hand side.

It is up to you to make sure your query is performant, if need be. It
is quite possible to build queries that will fail to run entirely on
Cassandra, as the [restrictions on Cassandra queries are quite
strict](http://www.datastax.com/dev/blog/a-deep-look-to-the-cql-where-clause).

[Keys](../root-type/keys.html) and
[indexes](../root-type/indexes.html) will aid query performance in an
intuitive manner. For finer details on just how your query will run,
please see the chapters on how your subdomain is translated to your
NoSQL backend ([Mongo](../mongo) and [Cassandra](../cassandra)).

{% assign prevTitle = "retrieval by key value" %}
{% assign prevLink = "retrieve-keyval.html" %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo.update" %}
{% assign nextLink = "update.html" %}
{% include navigate.html %}
