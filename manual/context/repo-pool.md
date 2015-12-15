---
title: the repo pool
layout: page
---
 
The longevity context provides three different sets of _repo pools_:

- one that goes against your application database (`context.repoPool`)
- one that goes against your test database (`context.testRepoPool`)
- one that goes against an in-memory database (`context.inMemTestRepoPool`)

We can retrieve the repositories from the pool by type:

    import longevity.persistence.Repo
    val userRepo: Repo[User] = context.repoPool[User]

{% assign prevTitle = "the longevity context" %}
{% assign prevLink = "../context" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "todo" %}
{% assign nextLink = "todo.html" %}
{% include navigate.html %}

