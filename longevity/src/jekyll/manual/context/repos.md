---
title: repositories in the context
layout: page
---
 
The longevity context provides three different [repositories](../repo):

- one that goes against your application database - `context.repo`
- one that goes against your test database - `context.testRepo`
- one that goes against an in-memory database - `context.inMemTestRepo`

You should easily be able to inject these repositories into whatever
dependency injection approach you are using. For instance, with
[Scaldi](http://scaldi.org/):

```scala
import longevity.persistence.Repo
import scaldi.Module

class PersistenceModule extends Module {
  bind[Repo[DomainModel]] to context.repo
}

class TestPersistenceModule extends Module {
  bind[Repo[DomainModel]] to context.testRepo
}
```

{% assign prevTitle = "configuring your longevity context" %}
{% assign prevLink  = "config.html" %}
{% assign upTitle   = "the longevity context" %}
{% assign upLink    = "." %}
{% assign nextTitle = "optimistic locking" %}
{% assign nextLink  = "opt-lock.html" %}
{% include navigate.html %}
