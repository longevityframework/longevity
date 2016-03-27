---
title: persistent entities
layout: page
---

As longevity is primarily a persistence framework, the common currency
of the longevity API is the `Persistent`, or persistent
entity. Persistent entities are part of your subdomain, and they are
also the _persistence unit_ - something that you can create, retrieve,
update or delete with a longevity [repository](repo). Persistent
entities are Scala case classes that meet some basic criteria. Those
criteria are laid out in the next few chapters. Here's a simple example:

```scala
import longevity.subdomain.persistent.Persistent

case class Note(
  author: String,
  content: String,
  dateCreated: DateTime)
extends Persistent
```

placeholder

{% assign prevTitle = "project setup" %}
{% assign prevLink = "project-setup.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle="building your subdomain" %}
{% assign nextLink="subdomain/" %}
{% include navigate.html %}

