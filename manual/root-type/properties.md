---
title: properties
layout: page
---

In our `RootEntity`, when we talk about the fields of the `Root` type,
we talk about properties, or `Props`. Properties follow a path from
the root, and take on the type of that field in the root. Here are some
examples:

{% gist sullivan-/e2151a996350786c0e27 %}

You need to specify the type of the property yourself, and longevity
will check that the type is correct when the property is created.

We recommend bundling them in an object within your root class, like
so:

{% gist sullivan-/b08a7e729227c8e1abdf %}

In principle, properties could map through any path from the root of
your aggregate, and have a wide variety of types. In practice, we
currently support properties with [basic
types](../subdomain/basics.html), [shorthand
types](../subdomain/shorthands.html), and
[associations](../subdomain/associations.html). While you can descend
into any contained entity, you currently cannot use collection types
such as `Option`, `Set`, and `List`. We plan to continue to expand the
supported properties types in the future.

Properties are used to build [keys](keys.html),
[indexes](indexes.html), and [queries](../repo/query.html). Presently,
you can use raw string paths anywhere you can use a `Prop`, but we
encourage you to use `Props` instead, as the string-based API will
probably go away in the future. The `Prop` allows us for expressive
typing while building [key values](../repo/retrieve-keyval.html) and
[queries](../repo/query.html), and we're looking to improve the type
safety of these features. We're also looking at a more fluent API for
referencing properties, possibly using
[dynamics](http://www.scala-lang.org/api/current/index.html#scala.Dynamic)
and/or
[macros](http://docs.scala-lang.org/overviews/macros/overview.html).

{% assign prevTitle = "the root type" %}
{% assign prevLink = "." %}
{% assign upTitle = "the root type" %}
{% assign upLink = "." %}
{% assign nextTitle = "keys" %}
{% assign nextLink = "keys.html" %}
{% include navigate.html %}

