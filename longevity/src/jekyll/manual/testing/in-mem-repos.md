---
title: in memory repositories
layout: page
---

Alongside `LongevityContext.repo` and `LongevityContext.testRepo`, there is an `inMemRepo` that you
can use for tests in which you want to use real repositories (as opposed to mocks or stubs), but you
don't want the overhead of working with a real database. While the in-memory repositories are fully
functional, the [query methods](../query) will probably not perform well in the face of large
amounts of data.

Setting up a test to use the in-memory repositories is easy
enough. For instance, suppose you have a user service that uses
constructor injection to get the user repository dependency:

```scala
class UserService(userRepo: Repo[DomainModel]) { // ...
```

And you typically initialize it like so:

```scala
val userService = new UserService(longevityContext.repo)
```

In your tests, you can initialize your service like this instead:

```scala
val userService = new UserService(longevityContext.inMemRepo)
```

{% assign prevTitle = "testing your domain model" %}
{% assign prevLink = "." %}
{% assign upTitle = "testing your domain model" %}
{% assign upLink = "." %}
{% assign nextTitle = "generating test data" %}
{% assign nextLink = "test-data.html" %}
{% include navigate.html %}
