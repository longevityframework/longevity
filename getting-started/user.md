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

The `User` case class provides us with the four members we find in the
UML in the [previous section](modelling.html), including the
relationship between `User` and `UserProfile`. There are also a couple
of business methods inside: `updateProfile` and `deleteProfile`.

In longevity terminology, `Users` are _persistent objects_ - that is,
objects we want to persist in their own table or collection. We tell
longevity that we want to persist them by marking them with the
`@persistent` annotation:

```scala
import longevity.model.annotations.persistent

@persistent(keySet = Set(
  partitionKey(User.props.username),
  key(User.props.email)))
// case class User ...
```

When we annotate `User` as a persistent object, longevity creates a
set of properties for us that we can use to reflect on `User`
fields. It puts these properties in an inner object `props` in the
`User` companion object. Now we can talk about `User` fields
`username` and `email` with properties `User.props.username` and
`User.props.email`.

We use `keySet` parameter on the `@persistent` annotation to tell
longevity about our keys. We define keys on the `username` and `email`
fields, specifying that these two member are to be unique: no two
users should have the same username or email.

You can have as many keys as you like, but only one of the keys - in
our case, the username key - can be a partition key. Partition keys
perform better than other keys when you are using a distributed
database, since the database can determine the node that holds the
data by examining the key.

{% assign prevTitle = "modelling our domain" %}
{% assign prevLink = "modelling.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="the user profile" %}
{% assign nextLink="user-profile.html" %}
{% include navigate.html %}
