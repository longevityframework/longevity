---
title: the domain model
layout: page
---

In longevity, we use the term [_domain
model_](https://en.wikipedia.org/wiki/Domain_model) to describe the
classes you want to persist.

There are two basic approaches you can take to describe your domain model
to longevity:

- marking the classes you want to persist with
[annotations](http://docs.scala-lang.org/tutorials/tour/annotations.html), or
- providing companion objects for your classes that describe how you
want to persist them.

We recommend you use the annotations-based approach, as it decidedly
cuts down on boilerplate code. These annotations are actually
[macros](http://docs.scala-lang.org/overviews/macros/annotations.html),
so you will need to include this line in your `build.sbt` to use them:

```scala
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
```

Everything you can do with the annotations, you can do with the
descriptor companion object approach as well. The examples in this
user manual will favor the annotations approach, but we will include
the equivalent companion object version every time a new annotation
feature is presented.

In this chapter, we will first look at how you can build all the
elements of your domain model. In the final section, we will see how
to gather all of these elements into a
`longevity.model.DomainModel`. Here's an overview:

- [Persistent Objects](persistents.html)
- [Basic Values](basics.html)
- [Collections](collections.html)
- [Components](components.html)
- [Key Values](key-values.html)
- [Limitations on Persistents, Embeddables, and Key Values](limitations.html)
- [Constructing a Domain Model](model.html)

{% assign prevTitle = "project setup" %}
{% assign prevLink  = "../prelims/project-setup.html" %}
{% assign upTitle   = "user manual" %}
{% assign upLink    = ".." %}
{% assign nextTitle = "persistent objects" %}
{% assign nextLink  = "persistents.html" %}
{% include navigate.html %}

