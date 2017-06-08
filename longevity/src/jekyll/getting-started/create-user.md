---
title: UserServiceImpl.createUser
layout: page
---

Here's the code for `UserServiceImpl.createUser`:

```scala
  def createUser(info: UserInfo): Future[UserInfo] = {
    {
      for {
        created <- repo.create(info.toUser)
      } yield {
        UserInfo(created.get)
      }
    } recover {
      case e: DuplicateKeyValException[_, _] => handleDuplicateKeyVal(e, info)
    }
  }
```

The heart of the method is the call to `repo.create`, inside the
for comprehension. `repo.create` returns a
`Future[PState[User]]`. The future is there because we want to treat
the underlying database call in an asynchronous fashion. The `User` is
further wrapped in a `PState`, or _persistent state_, which
contains persistence information about the user that is not part of
the domain model. You don't need to know much of anything about a
`PState`, except that you can call methods `get` and `map` on it, to
work with the underlying `User` inside.

In the yield clause of the for comprehension in this method,
`created.get` retrieves the `User` from the `PState`. This in turn is
passed to a method that converts from a `User` to a `UserInfo`. Then
the for comprehension wraps this back up in a `Future`, which is
exactly the kind of thing that Akka HTTP wants to work with.

One caveat here is that `repo.create` might fail with a duplicate
key exception. There might already be a user that has either the same
username or email. So we call `recover` on the resulting `Future` and
convert the longevity `DuplicateKeyValException` into a service-level
exception: either `DuplicateUsernameException` or
`DuplicateEmailException`:

```scala
  import longevity.exceptions.persistence.DuplicateKeyValException

  /** converts longevity duplicate key val exception into simbl exception */
  private def handleDuplicateKeyVal(e: DuplicateKeyValException[_, _], info: UserInfo): Nothing = {
    e.key match {
      case User.keys.username =>
        throw new DuplicateUsernameException(info.username)
      case User.keys.email =>
        throw new DuplicateEmailException(info.email)
    }
  }
```

Our [Akka HTTP route](routes.html) responds to these exceptions by
producing a `409 Conflict`.

{% assign prevTitle = "the user service implementation" %}
{% assign prevLink = "service-impl.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="UserServiceImpl.retrieveUser" %}
{% assign nextLink="retrieve-user.html" %}
{% include navigate.html %}
