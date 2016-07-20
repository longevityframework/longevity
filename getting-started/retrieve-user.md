---
title: UserServiceImpl.retrieveUser
layout: page
---

<a href="#code/src/main/scala/simbl/service/UserServiceImpl.scala"
class="shortcut">`UserServiceImpl.retrieveUser`</a> does its work by
calling `userRepo.retrieve`. To call this method, we have to convert
from the `username` string to a `Username`, as `userRepo.retrieve`
takes a `KeyVal` as argument.

Once again, the `User` is wrapped in a `PState`, so we can manipulate
its persistent state if we wish. This in turn is wrapped in an
`Option`, as there may or may not be a user with that username. This
in turn is wrapped in a `Future`, as we want to treat the database
call in an asynchronous fashion. This feels like a lot of layers of
wrapping, but they are not too painful to work with if you use for
comprehensions.

Once the user is retrieved, we still need to map the
`Option[PState[User]]` to an `Option[UserInfo]`. This is done in two
lines in the `yield` clause of the for comprehension. When
`retrieveUser` returns a future `None`, Akka HTTP will generate a `404
Not Found`

{% assign prevTitle = "UserServiceImpl.createUser" %}
{% assign prevLink = "create-user.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="UserServiceImpl.updateUser" %}
{% assign nextLink="update-user.html" %}
{% include navigate.html %}
