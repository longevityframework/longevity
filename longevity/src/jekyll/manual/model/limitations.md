---
title: limitations on persistents, components, and key values
layout: page
---

Longevity currently places the following limitations on the kinds of
classes you can use for your [persistents](persistents.html),
[components](components.html), and [key values](key-values.html):

- They must be a case class or a case object.
- They must not be an [inner class](http://docs.scala-lang.org/tutorials/tour/inner-classes.html).
- Case class primary constructors must have a single parameter list.

We would like to relax these limitations in the future. If you find
these limitations to be too cumbersome for you, please [let us
know](http://longevityframework.org/discussions.html)
what you are trying to do, and we will see what we can to do help.
Just keep in mind that, whatever possibilities we allow for, longevity
has the following requirements:

- The list of properties that a persistent, components, or key value
  contains must be clearly defined.
- We must be able to retrieve a property value from a persistent,
  component, or key value.
- We must be able to construct a new persistent, component, or key
  value from a complete set of property values.

Case classes are quite convenient things for fulfilling the requirements we have. They also seem a
natural choice for modeling out a domain. We chose to start with them for these reasons.

Update: It's beginning to look like the types that will be accepted here are those types that make up
[ADTs](https://en.wikipedia.org/wiki/Algebraic_data_type) that are recognizable to
[shapeless](https://github.com/milessabin/shapeless).

{% assign prevTitle = "key values" %}
{% assign prevLink  = "key-values.html" %}
{% assign upTitle   = "the domain model" %}
{% assign upLink    = "." %}
{% assign nextTitle = "the persistent type" %}
{% assign nextLink  = "../ptype" %}
{% include navigate.html %}
