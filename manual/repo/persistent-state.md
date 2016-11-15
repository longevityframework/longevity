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
aggregate using `PState.map`:

```scala
val updatedState: PState[User] = userState.map(_.copy(title = Some(newTitle)))
```

Only isolated portions of your program will need to concern themselves
with persistence state. For instance, suppose we have a domain service
method to update a user's score card:

```scala
trait UserService {
  def updateScoreCard(user: User, event: PointScoredEvent): User
}
```

You can easily wrap a service call into `PState.map`:

```scala
val updatedState: PState[User] = userState.map { user =>
  userService.updateScoreCard(user, event)
}
```

We have just provided a guarantee - at virtually no cost - that
persistence concerns can not leak into the `UserService`
implementation.

{% assign prevTitle = "repositories" %}
{% assign prevLink  = "." %}
{% assign upTitle   = "repositories" %}
{% assign upLink    = "." %}
{% assign nextTitle = "persistent state wrappers" %}
{% assign nextLink  = "pstate-wrappers.html" %}
{% include navigate.html %}
