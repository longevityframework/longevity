---
title: what is longevity?
layout: page
---

At it's heart, longevity is a persistence framework. Our aim is to do all the persistence work for
you, and to present persistence via a clean API that helps you isolate persistence concerns from
other parts of your application. This is a big win for you, as building and maintaining a
persistence layer is typically a major effort when writing database applications.

To accomplish this, you start by constructing your [_domain model_](../model) - those classes that
compose the structures you want to persist - using conventional Scala language elements such as case
classes, sealed traits, options, and immutable sets and lists. Then, mark your domain classes with
longevity annotations to describe the roles they play in the model.

Once you have set up your domain model, you pass it back to longevity to get your
`LongevityContext`. The main thing we provide to you with the [longevity context](../context) is the
[repository](../repo), which presents a complete persistence API, including CRUD operations
(create/retrieve/update/delete) for all your persistent objects, and [queries](../query) that return
multiple results.

The longevity context provides you with other tools that we generally consider part of the
persistence layer. We provide you with a set of repositories that persist to a test database, as
well as fully functional in-memory repositories that you can use for testing. There are integration
tests that will exercise all of the repository CRUD operations, test data generators, and a
framework for testing queries.

{% assign prevTitle = "preliminaries" %}
{% assign prevLink  = "." %}
{% assign upTitle   = "preliminaries" %}
{% assign upLink    = "." %}
{% assign nextTitle = "project setup" %}
{% assign nextLink  = "project-setup.html" %}
{% include navigate.html %}
