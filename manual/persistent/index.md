---
title: persistent objects
layout: page
---

As longevity is primarily a persistence framework, the common currency
of the longevity API is the `Persistent`, or persistent
object. Persistent objects are part of your subdomain, and they are
also the _persistence unit_ - something that you can create, retrieve,
update or delete with a longevity [repository](../repo).

Persistent objects are Scala case classes that meet some basic
criteria. Those criteria are laid out in the chapters that follow.

Here's a simple example:

``` scala
import longevity.subdomain.Persistent
import org.joda.time.DateTime

case class Note(
  author: String,
  content: String,
  dateCreated: DateTime)
extends Persistent
```

{% assign prevTitle = "building your subdomain" %}
{% assign prevLink = "../subdomain.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "persistent types" %}
{% assign nextLink = "ptypes.html" %}
{% include navigate.html %}
