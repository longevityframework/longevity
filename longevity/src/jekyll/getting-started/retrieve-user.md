---
title: UserServiceImpl.retrieveUser
layout: page
---

Here's the code for `UserServiceImpl.retrieveUser`:

```scala
  def retrieveUser(username: String): Future[Option[UserInfo]] = {
    for {
      retrieved <- repo.retrieve[User](Username(username))
    } yield {
      def stateToInfo(state: PState[User]) = UserInfo(state.get)
      retrieved.map(stateToInfo)
    }
  }
```

This method does its work by calling `repo.retrieve`. To call this method, we have to convert from
the `username` string to a `Username`, as `repo.retrieve` only takes a key value as argument. The
type safety is assured by an implicit argument `longevity.model.ptype.Key[SimblDomainModel, User,
Username]` to the method. This is why we made `User.usernameKey` an implicit value.

Once again, the `User` is wrapped in a `PState`, so we can manipulate
its persistent state if we wish. This in turn is wrapped in an
`Option`, as there may or may not be a user with that username. This
in turn is wrapped in a `Future`, as we want to treat the database
call in an asynchronous fashion. This feels like a lot of layers of
wrapping, but they are not too painful to work with if you use for
comprehensions.

Once the user is retrieved, we still need to map the `Option[PState[User]]` to an
`Option[UserInfo]`. This is done in two lines in the `yield` clause of the for comprehension. If
`retrieveUser` returns a future `None`, Akka HTTP will generate a `404 Not Found`

{% assign prevTitle = "UserServiceImpl.createUser" %}
{% assign prevLink  = "create-user.html" %}
{% assign upTitle   = "getting started guide" %}
{% assign upLink    = "." %}
{% assign nextTitle = "UserServiceImpl.updateUser" %}
{% assign nextLink  = "update-user.html" %}
{% include navigate.html %}
