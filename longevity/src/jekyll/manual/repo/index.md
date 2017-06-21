---
title: the repository
layout: page
---

All persistence operations occur through the repository - the `longevity.persistence.Repo`. In this
chapter, we will take a look at the schema creation operation, as well as the standard CRUD
(create/retrieve/update/delete) operations. Repository queries are discussed in the [following
chapter](../query). How to get your hands on a `Repo` instance is discussed in the section on
[repositories](../context/repos.html) in the chapter on the longevity context.

- [Opening and Closing the Connection](connection.html)
- [Schema Creation](schema-creation.html)
- [Persistent State](persistent-state.html)
- [Persistent State Wrappers](pstate-wrappers.html)
- [Repo.create](create.html)
- [Creating Many Aggregates at Once](create-many.html)
- [Retrieval by Key Value](retrieve.html)
- [Repo.update](update.html)
- [Repo.delete](delete.html)
- [Persisting Polymorphism](poly.html)

{% assign prevTitle = "controlled vocabularies" %}
{% assign prevLink  = "../poly/cv.html" %}
{% assign upTitle   = "user manual" %}
{% assign upLink    = ".." %}
{% assign nextTitle = "opening and closing the connection" %}
{% assign nextLink  = "connection.html" %}
{% include navigate.html %}
