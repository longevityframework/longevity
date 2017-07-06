---
title: user service implementation
layout: page
---

Implementations for the methods in `UserService` are provided in `UserServiceImpl.scala`. To do its
job, the `UserServiceImpl` needs a `SimblDomainModel` repository, which is retrieved from the
`LongevityContext` in `SimblContextImpl.scala` like so:

```scala
val longevityContext = LongevityContext[Future, SimblDomainModel]()
val repo = longevityContext.repo
```

The `LongevityContext` takes an implicit `ExecutionContext` when constructed with a `Future` effect.
This will be used for all the repository operations. But `UserServiceImpl` will still need an
execution context to compose the futures. Akka HTTP already needs an `ExecutionContext` to run, and
`SimblContextImpl` provides an execution context that it pulls out of the Akka `ActorSystem`.

Here's the scaffolding for `UserServiceImpl`, which shows how the class acquires its two
dependencies:

```scala
package simbl.service

import longevity.persistence.Repo
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import simbl.domain.SimblDomainModel

/** default implementation of service to back the [[UserRoute user routes]] */
class UserServiceImpl(
  private val repo: Repo[Future, SimblDomainModel])(
  implicit context: ExecutionContext)
extends UserService {

  // ...

}
```

There are a number of service methods in `UserServiceImpl`. In this
tutorial, we will focus on three: `createUser`, `retrieveUser`, and
`updateUser`.

{% assign prevTitle = "the user service" %}
{% assign prevLink = "service.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="UserServiceImpl.createUser" %}
{% assign nextLink="create-user.html" %}
{% include navigate.html %}
