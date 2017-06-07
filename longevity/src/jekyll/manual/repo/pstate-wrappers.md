---
title: persistent state wrappers
layout: page
---

Longevity provides three type aliases, `FPState[P]`, `OPState[P]`, and
`FOPState[P]`, for types `Future[PState[P]]`, `Option[PState[P]]`, and
`Future[Option[PState[P]]]`, respectively. You can use these type
aliases as shorthands for the longer type names.

We also provide implicit classes that provide extra methods `mapP` and
`flatMapP` for each of the three types above. These methods will open
up the containing `Future`/`Option` combination, apply a function from
`P` to `P`, and wrap the result back up. The methods for `OPState[P]`
would look something like this:

```scala
class OPState[P <: Persistent] {

  /** map the optional `PState` by mapping the `Persistent` inside */
  def mapP(f: P => P): OPState[P]

  /** flatMap the optional `PState` by flat-mapping the `Persistent` inside */
  def flatMapP(f: P => Option[P]): OPState[P]

}
```

`FOPState[P]` also has convenience methods `mapState` and
`flatMapState` for opening up the enclosed option:

```scala
class FOPState[P <: Persistent] {

  /** map the `FOPState` by mapping the `Persistent` inside the `PState` */
  def mapP(f: P => P): FOPState[P]

  /** flatMap the `FOPState` by flat-mapping the `Persistent` inside */
  def flatMapP(f: P => Future[P]): FOPState[P]

  /** map the `FOPState` by mapping the `PState` inside */
  def mapState(f: PState[P] => PState[P]): FOPState[P]

  /** flatMap the `FOPState` by flat-mapping the `PState` inside */
  def flatMapState(f: PState[P] => FPState[P]): FOPState[P]

}
```

Consider the following method, where we are tasked with attempting to
retrieve a user by username, modifying the last name _if_ we find such
a user, and persisting the result. We return `true` if we updated a
user, and `false` if not. We can use `FOPState` extension methods to
our advantage here. The type ascriptions would normally be left out,
but we include them to make the example easier to follow:

```scala
def updateLastName(username: Username, newLastName: String): Future[Boolean] = {
  val retrieved: FOPState[User] = repo.retrieve[User](username)
  val modified: FOPState[User] = retrieved.mapP(_.copy(lastName = newLastName))
  val updated: FOPState[User] = modified.flatMapState(repo.update)
  updated.map(_.nonEmpty)
}
```

{% assign prevTitle = "persistent state" %}
{% assign prevLink  = "persistent-state.html" %}
{% assign upTitle   = "the repository" %}
{% assign upLink    = "." %}
{% assign nextTitle = "repo.create" %}
{% assign nextLink  = "create.html" %}
{% include navigate.html %}
