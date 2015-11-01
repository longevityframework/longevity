---
title: limitations on entities and shorthands
layout: page
---

Longevity currently places the following limitations on the kinds of
classes you can use for your [entites](entities.html) and
[shorthands](shorthands.html):

- They must be a case class
- They must not be an [inner class](http://docs.scala-lang.org/tutorials/tour/inner-classes.html)
- They must have a primary constructor with a single parameter list
- The primary constructor for a shorthand must have a single parameter
  of a [basic type](basics.html)

We plan to relax these limitations in the future. If you find these
limitations to be too cumbersome for you, please let us know! Just
keep in mind that, whatever possibilities we allow for, longevity must
be able to perform the following operations:

- Retrieve a property value from an entity instance
- Retrieve an abbreviated value from a shorthand instance
- Construct a new entity instance from a complete set of property values
- Construct a new shorthand instance from an abbreviated value.

Case classes are quite convenient things for fulfilling the
requirements we have. They also seem quite natural for modeling out a
domain. We chose to start with them for these reasons.

TODO: link to discussions page for "please let us know" remarks
throughout the user man

{% assign prevTitle = "entities and value objects" %}
{% assign prevLink = "value-objects.html" %}
{% assign upTitle = "building your subdomain" %}
{% assign upLink = "." %}
{% assign nextTitle = "assocations" %}
{% assign nextLink = "associations.html" %}
{% include navigate.html %}
