---
title: shorthand pools
layout: page
---

You can think of a `ShorthandPool` as an immutable set of
`Shorthands`. You can create an empty one like this:

    val pool = ShorthandPool()

Or like this:

    val pool = ShorthandPool.empty

You can put a number of shorthands in a pool like so:

    val pool = ShorthandPool(emailShorthand, markdownShorthand, uriShorthand)

And you can build them with the `+` operator as well:

    val pool = ShorthandPool() + emailShorthand + markdownShorthand + uriShorthand

Shorthand pools are needed to construct your `RootTypes`, as
well as your `Subdomains`. We normally provide the pool implicitly,
like so:

{% gist sullivan-/5bd434d757dc64b6caac %}

If you want to be explicit about it, you can do it like so:

{% gist sullivan-/76f4dbb5f99af7eaf090 %}

If you don't supply a `ShorthandPool`, an empty one will be provided
for you. If you make use of a shorthand that's not in the pool, you
won't find out until some time after your `Subdomain` is
constructed. [We plan to remedy
things](https://www.pivotaltracker.com/story/show/99755864) so that
these kinds of errors get reported on construction of your `Subdomain`
or your `RootType`. In the meantime, longevity provides [test
support](../testing.html) to exercise persistence operations on your
`Subdomain`, and this kind of error will be exposed by these tests.

{% assign prevTitle = "shorthands" %}
{% assign prevLink = "." %}
{% assign upTitle = "shorthands" %}
{% assign upLink = "." %}
{% assign nextTitle = "where not to construct your shorthand pools" %}
{% assign nextLink = "where-not.html" %}
{% include navigate.html %}

