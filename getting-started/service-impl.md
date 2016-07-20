---
title: user service implementation
layout: page
---

Implementations for the methods in
<a href="#code/src/main/scala/simbl/service/UserService.scala"
class="shortcut">`UserService.scala`</a> are
provided in 
<a href="#code/src/main/scala/simbl/service/UserServiceImpl.scala"
class="shortcut">`UserServiceImpl.scala`</a>. To
do its job, the `UserServiceImpl` needs a user
repository, which is retrieved from
the `LongevityContext` in 
<a href="#code/src/main/scala/simbl/SimblContextImpl.scala"
class="shortcut">`SimblContextImpl.scala`</a>. Because most of the
repository methods need an execution context to run in,
`SimblContextImpl` also provides an execution context that it pulls
out of the Akka `ActorSystem`. Akka HTTP already needs an
`ExecutionContext` to run. In other scenarios, you can always find an
execution context in
`scala.concurrent.ExecutionContext.Implicits.global`.

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
