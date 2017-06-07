---
title: repo pools
layout: page
---
 
The longevity context provides three different _repo pools_:

- one that goes against your application database (`context.repoPool`)
- one that goes against your test database (`context.testRepoPool`)
- one that goes against an in-memory database (`context.inMemTestRepoPool`)

We can retrieve the repositories from the pool by type:

```scala
import longevity.persistence.Repo
val userRepo: Repo[User] = context.repoPool[User]
```

You should easily be able to inject these repositories into whatever
dependency injection approach you are using. For instance, with
[Scaldi](http://scaldi.org/):

```scala
import longevity.persistence.Repo
import scaldi.Module

class PersistenceModule extends Module {
  bind[Repo[User]] to context.repoPool[User]
  bind[Repo[Blog]] to context.repoPool[Blog]
  bind[Repo[BlogPost]] to context.repoPool[BlogPost]
}

class TestPersistenceModule extends Module {
  bind[Repo[User]] to context.testRepoPool[User]
  bind[Repo[Blog]] to context.testRepoPool[Blog]
  bind[Repo[BlogPost]] to context.testRepoPool[BlogPost]
}
```

We'll look at schema creation next, since that responsibility falls to
the `RepoPool`.

{% assign prevTitle = "configuring your longevity context" %}
{% assign prevLink  = "config.html" %}
{% assign upTitle   = "the longevity context" %}
{% assign upLink    = "." %}
{% assign nextTitle = "optimistic locking" %}
{% assign nextLink  = "opt-lock.html" %}
{% include navigate.html %}
