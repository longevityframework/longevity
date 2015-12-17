---
title: limitations on entities and shorthands
layout: page
---

Longevity currently places the following limitations on the kinds of
classes you can use for your [entites](entities.html) and
[shorthands](shorthands.html):

- They must be a case class.
- They must not be an [inner class](http://docs.scala-lang.org/tutorials/tour/inner-classes.html).
- They must have a primary constructor with a single parameter list.
- The primary constructor for a shorthand must have a single parameter
  of a [basic type](basics.html).

We would like to relax these limitations in the future. If you find
these limitations to be too cumbersome for you, please let us know
what you are trying to do, and we will see what we can to do help.
Just keep in mind that, whatever possibilities we allow for, longevity
has the following requirements:

- The set of properties that an entity contains must be clearly defined.
- We must be able to retrieve a property value from an entity instance.
- We must be able to construct a new entity instance from a complete set of property values.
- We must be able to retrieve an abbreviated value from a shorthand instance.
- We must be able to construct a new shorthand instance from an abbreviated value.

Case classes are quite convenient things for fulfilling the
requirements we have. They also seem a natural choice for modeling out
a domain. We chose to start with them for these reasons.

TODO: link to discussions page for "please let us know" remarks
throughout the user man

{% assign prevTitle = "entities and value objects" %}
{% assign prevLink = "value-objects.html" %}
{% assign upTitle = "entities" %}
{% assign upLink = "." %}
{% assign nextTitle = "assocations" %}
{% assign nextLink = "../associations" %}
{% include navigate.html %}
