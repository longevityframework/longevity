---
title: repo pools
layout: page
---
 
The longevity context provides three different _repo pools_:

- one that goes against your application database (`context.repoPool`)
- one that goes against your test database (`context.testRepoPool`)
- one that goes against an in-memory database (`context.inMemTestRepoPool`)

We can retrieve the repositories from the pool by type:

    import longevity.persistence.Repo
    val userRepo: Repo[User] = context.repoPool[User]

You should easily be able to inject these repositories into whatever
dependency injection approach you are using. For instance, with
[Scaldi](http://scaldi.org/):

{% gist sullivan-/8b582592a94b14b61c80 %}

The `Repo` API makes heavy use of the persistent state, or `PState`,
so we will take a look at that before moving on to repositories.

{% assign prevTitle = "the longevity context" %}
{% assign prevLink = "." %}
{% assign upTitle = "the longevity context" %}
{% assign upLink = "." %}
{% assign nextTitle = "persistent state" %}
{% assign nextLink = "persistent-state.html" %}
{% include navigate.html %}
