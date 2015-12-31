---
title: repo.retrieve
layout: page
---

You can look up any aggregates from the database using the
[keys](../root-type/keys.html) you defined in your `RootEntity`. You
just have to get a `KeyVal` out of the `Key`, which you can do by
supplying values for each of the properties of the key, in turn. For
example:

{% gist sullivan-/8c2a2cd6cfffaea8c59c %}

`Key.apply` with throw a `KeyValException` if the types of the
supplied arguments do not match the types of the properties of the
key. Of course, we [would prefer this to be better
typed](https://www.pivotaltracker.com/story/show/109682804), so as to
fail here at compile time.

You can also pull a `KeyVal` out of an existing `Root` if you so
desire:

    val user: User = parseUserFromJson(json)
    val userKeyVal: KeyVal[User] = User.usernameKey.keyValForRoot(user)

(The name of this method is intentionally long, as it doesn't seem
a very useful method.)

Once you have your `KeyVal`, you can look up the aggregate in the
database. You get an `Option` back, as the `KeyVal` does not
necessarily match an existing aggregate.

{% gist sullivan-/e6c10d7b17b4a16b1119 %}

Once you get back your `PState`, you can of course use it to examine
the aggregate itself with `PState.get`. You can modify it with
`PState.map`, and you can pass the state on to
[`Repo.update`](repo-update.html) or
[`Repo.delete`](repo-delete.html).

`Repo.retrieve` will always result in a database call. Longevity will
not cache versions for you and pull them from the cache. Not caching
provides a guarrantee that the retrieved aggregate is up to date with
the latest state of the database, and reduces the chances of a write
collision. We may revisit this in the future, but we do not consider
it an excessive burden on the longevity user to employ their own
cache, if need be.

{% assign prevTitle = "creating many aggregates at once" %}
{% assign prevLink = "create-many.html" %}
{% assign upTitle = "the longevity context" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo.retrieveByQuery" %}
{% assign nextLink = "repo-query.html" %}
{% include navigate.html %}
