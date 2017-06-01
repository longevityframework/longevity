---
title: UserServiceImpl.updateUser
layout: page
---

Here's the code for `UserServiceImpl.updateUser`:

```scala
  def updateUser(username: String, info: UserInfo): Future[Option[UserInfo]] = {
    {
      for {
        retrieved <- userRepo.retrieveOne(Username(username))
        modified = retrieved.map(info.mapUser)
        updated <- userRepo.update(modified)
      } yield {
        Some(UserInfo(updated.get))
      }
    } recover {
      case e: DuplicateKeyValException[_] => handleDuplicateKeyVal(e, info)
      case e: NoSuchElementException => None
    }
  }
```

This method shows a variation on `userRepo.retrieve`:
`userRepo.retrieveOne`. `retrieveOne` opens up the `Option` for you,
throwing a `NoSuchElementException` if the `Option` is empty. We
handle the `NoSuchElementException` in the `recover` clause, returning
`None` if the `User` was not found. Akka HTTP will handle the `None`
by returning a `404 Not Found`, which is exactly what we want.

The `retrieved` in the for comprehension is a `PState[User]`. Calling
`retrieved.map` produces another `PState[User]` that reflects the
changes produced by the function passed to `map`. In this case, we
call `UserInfo.mapUser`, which updates a `User` according to the
information in the `UserInfo`. The resulting `PState` is stored in a
local value named `modified`.

We then pass `modified` on to `userRepo.update`. This method persists
the changes, but like `userRepo.create`, it might generate a
`DuplicateKeyValException` if we try to update the user to have a
conflicting username or email. Once again, we handle this problem in
the `recover` clause, converting the longevity exception into a Simple
Blogging service exception.

{% assign prevTitle = "UserServiceImpl.retrieveUser" %}
{% assign prevLink = "retrieve-user.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle = "exercising the api" %}
{% assign nextLink = "api.html" %}
{% include navigate.html %}
