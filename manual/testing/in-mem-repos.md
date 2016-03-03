---
title: in memory repositories
layout: page
---

Alongside the `repoPool` provided by the `LongevityContext` there is
an `inMemRepoPool` that you can use for tests in which you want to use
real repositories (as opposed to mocks or stubs), but you don't want
the overhead of working with a real database. While the in-memory
repositories are fully functional, the `Repo.retrieveByQuery` method
will probably not perform well in the face of large amounts of data.

Setting up a test to use the in-memory repositories is easy
enough. For instance, suppose you have a user service that uses
constructor injection to get the user repository dependency:

    class UserService(userRepo: Repo[User]) { // ...

And you typically initialize it like so:

    val userService = new UserService(longevityContext.repoPool[User])

In your tests, you can initialize your service like this instead:

    val userService = new UserService(longevityContext.inMemRepoPool[User])

{% assign prevTitle = "testing" %}
{% assign prevLink = "." %}
{% assign upTitle = "testing" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo crud spec" %}
{% assign nextLink = "repo-crud-spec.html" %}
{% include navigate.html %}
