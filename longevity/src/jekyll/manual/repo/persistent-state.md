---
title: persistent state
layout: page
---

The persistent state is a container for your persistent objects where
the persistence information is stored. You might find database IDs,
optimistic locking counters, dirty flags, and created/modified columns
in there. While you will be able to see some of these things when
querying your database by hand, in your application, the persistent
state is a black box. But you can always get at your persistent object
with method `get`:

```scala
val userState: PState[User] = getUserState()
val user: User = userState.get
```

Your persistent object is immutable and so is the `PState`. You
"update" a persistent object by creating a new, modified object, for
instance with method `copy`:

```scala
val newTitle = "Dr."
val updatedUser: User = user.copy(title = Some(newTitle))
```

But just as this operation would not cause any change to an immutable
collection that the user was in, neither will it cause any change to
the `userState`. You can make the `PState` aware of changes to your
aggregate using `PState.modify`:

```scala
val updatedState: PState[User] = userState.modify(_.copy(title = Some(newTitle)))
```

Only isolated portions of your program will need to concern themselves
with persistence state. For instance, suppose we have a domain service
method to update a user's score card:

```scala
trait UserService {
  def updateScoreCard(user: User, event: PointScoredEvent): User
}
```

You can easily wrap a service call into `PState.modify`:

```scala
val updatedState: PState[User] = userState.modify { user =>
  userService.updateScoreCard(user, event)
}
```

We have just provided a guarantee - at virtually no cost - that
persistence concerns can not leak into the `UserService`
implementation.

What if our service method was an effectful method? Perhaps it has to hit another blocking service
somewhere to do its work, such as when hitting another
[microservice](https://www.martinfowler.com/articles/microservices.html) in the application? In
this case, we would probably be wrapping it in a future-like construct in a reactive setting, or in
an IO monad construct in more of a functional setting. Let's use `scala.concurrent.Future` as an
example:

```scala
trait UserService {
  def updateScoreCard(user: User, event: PointScoredEvent): Future[User]
}
```

We can change our above example to call `modifyF` instead of `modify`, like so:

```scala
val updatedState: Future[PState[User]] = userState.modifyF { user =>
  userService.updateScoreCard(user, event)
}
```

The persistent state does not inherit the effect from the repository. This means you will have to
make an effect implicitly available when calling `modifyF`, in the same way as when you [created
your longevity context](../context/effect.html). (The persistent state could easily inherit the
effect from the repository, and perhaps it should. The advantage of inheriting the effect is that
the user would not need to supply the effect again when calling `modifyF`. The disadvantage is that
the type gets more cumbersome. For instance, `PState[User]` would become `PState[Future, User]`.)

{% assign prevTitle = "schema creation" %}
{% assign prevLink  = "schema-creation.html" %}
{% assign upTitle   = "the repository" %}
{% assign upLink    = "." %}
{% assign nextTitle = "repo.create" %}
{% assign nextLink  = "create.html" %}
{% include navigate.html %}
