---
title: building the user aggregate
layout: page
---

The user has four parts: the `User`, the `UserProfile`, and two
natural keys: the `Username` and the `Email`.  Let's focus on the
`User` first:

```scala
package simbl.domain

case class User(
  username: Username,
  email: Email,
  fullname: String,
  profile: Option[UserProfile]) {

  def updateProfile(profile: UserProfile): User = copy(profile = Some(profile))

  def deleteProfile: User = copy(profile = None)

}
```

The `User` case class provides us with the four members we find in the UML in the section on
[modelling our domain](modelling.html), including the relationship between `User` and `UserProfile`.
There are also a couple of business methods inside: `updateProfile` and `deleteProfile`.

In longevity terminology, `Users` are _persistent objects_ - that is,
objects we want to persist in their own table or collection. We tell
longevity that we want to persist them by marking them with the
`@persistent` annotation:

```scala
package simbl.domain

import longevity.model.annotations.persistent

@persistent[SimblDomainModel]
case class User(
  username: Username,
  email: Email,
  fullname: String,
  profile: Option[UserProfile]) {

  def updateProfile(profile: UserProfile): User = copy(profile = Some(profile))

  def deleteProfile: User = copy(profile = None)

}
```

When we annotate `User` as a persistent class, longevity augments the `User` companion object,
making it extend `longevity.model.PType[SimblDomainModel, User]`. It also creates a set of
properties for us that we can use to reflect on `User` fields. It puts these properties in an inner
object `props` in the `User` companion object. Now we can talk about `User` fields `username` and
`email` with properties `User.props.username` and `User.props.email`.

In our companion object, we define keys on the `username` and `email`
fields, specifying that these two member are to be unique: no two
users should have the same username or email.

```scala
object User {
  implicit val usernameKey = primaryKey(props.username)
  implicit val emailKey = key(props.email)
}
```

The `props.username` and `props.email` are two of the properties fields that were put in by the
`@persistent` annotation. The methods `primaryKey` and `key` are provided by the `PType` class that
the `User` companion object now extends.

You can have as many keys as you like, but only one of the keys - in
our case, the username key - can be a primary key. Primary keys
perform better than other keys when you are using a distributed
database, since the database can determine the node that holds the
data by examining the key.

{% assign prevTitle = "declaring the domain model" %}
{% assign prevLink  = "building.html" %}
{% assign upTitle   = "getting started guide" %}
{% assign upLink    = "." %}
{% assign nextTitle = "the user profile" %}
{% assign nextLink  = "user-profile.html" %}
{% include navigate.html %}
