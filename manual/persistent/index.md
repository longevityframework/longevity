---
title: persistent objects
layout: page
---

As longevity is primarily a persistence framework, the common currency
of the longevity API is the `Persistent`, or persistent
object. Persistent objects are part of your subdomain, and they are
also the _persistence unit_ - something that you can create, retrieve,
update or delete with a longevity [repository](../repo). Persistent
entities are Scala case classes that meet some basic criteria. Those
criteria are laid out in the following chapters:

- TODO rehash this ToC
- [Shorthands](../shorthands)
- [Entities](../entities)
- [Associations](../associations)

Here's a simple example:

``` scala
import longevity.subdomain.persistent.Persistent
import org.joda.time.DateTime

case class Note(
  author: String,
  content: String,
  dateCreated: DateTime)
extends Persistent
```

Persistent objects are fleshed out in the following sections of this chapter:

- [Kinds of Persistent Objects](kinds.html)
- [Persistent Types](ptypes.html)

{% assign prevTitle = "building your subdomain" %}
{% assign prevLink = "../subdomain.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "kinds of persistent objects" %}
{% assign nextLink = "kinds.html" %}
{% include navigate.html %}
