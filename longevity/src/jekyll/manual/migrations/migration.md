---
title: defining your migration
layout: page
---

Once you have tags for both the initial and final versions of your domain model, you can now proceed
to build your migration. Migrations are composed of migration steps of three kinds:

- Drop a persistent type that exists in the initial model.
- Create a new persistent type that does not exist in the initial model
- Update a persistent type by means of a Scala function.

We'll walk through an example here that contains one of each of these migration steps. Our initial
model consists of users and groups of users:

```scala
package migrations.version_0_1

import longevity.annotations.domainModel
import longevity.annotations.keyVal
import longevity.annotations.persistent

@domainModel trait Model

@persistent[Model]
case class User(username: Username, last: String, first: String)

object User {
  implicit val usernameKey = primaryKey(props.username)
}

@keyVal[Model, User]
case class Username(value: String)

@persistent[Model]
case class UserGroup(groupName: GroupName, members: Set[Username])

object UserGroup {
  implicit val groupNameKey = primaryKey(props.groupName)
}

@keyVal[Model, UserGroup]
case class GroupName(value: String)
```

In the time since this version of the model was released in production, we have gone ahead and made
some changes to the domain. We decided to drop the concept of groups, and we've added blogs. We've
also made some changes to the user type by extracting the last and first name into a
[component](../model/components.html). As we prepare to release the latest code to production, we
tag this new version of the model as `version_0_2`:

```scala
package migrations.version_0_2

import longevity.annotations.component
import longevity.annotations.domainModel
import longevity.annotations.keyVal
import longevity.annotations.persistent

@domainModel trait Model

@persistent[Model]
case class User(username: Username, fullname: Fullname)

object User {
  implicit val usernameKey = primaryKey(props.username)
}

@keyVal[Model, User]
case class Username(value: String)

@component[Model]
case class Fullname(last: String, first: String)

@persistent[Model]
case class Blog(
  uri: BlogUri,
  title: String,
  authors: Set[Username])

object Blog {
  implicit val uriKey = primaryKey(props.uri)
}

@keyVal[Model, Blog]
case class BlogUri(uri: String)
```

In order to build out the update step for `User`, we need to define a function that will convert
from type `version_0_1.User` to `version_0_2.User`. We might do this like so:

```scala
import migrations.version_0_1.{ User     => User1 }
import migrations.version_0_1.{ Username => Username1 }
import migrations.version_0_2.{ Fullname => Fullname2 }
import migrations.version_0_2.{ User     => User2 }
import migrations.version_0_2.{ Username => Username2 }

def updateUsername(username1: Username1) = Username2(username1.value)

def updateUser(user1: User1) =
  User2(
    updateUsername(user1.username),
    Fullname2(user1.last, user1.first))
```

To build a migration, we will need to specify a name for our initial version, of type
`Some[String]`, and a name for our final version, of type `String`. These values should match what
is found in the `longevity.modelVersion` [configuration flag](../context/config.html). In our case,
we are applying our first migration, so `longevity.modelVersion` is set to `None`. We decide to call
our final version `version_0_2`. We are now ready to build our migration, like so:

```scala
import longevity.migrations.Migration

val 0_1_to_0_2 =
  Migration.build[version_0_1.Model, version_0_2.Model](None, "version_0_2")
    .drop[version_0_1.UserGroup]
    .create[version_0_2.Blog]
    .update(updateUser)
    .build
```

There are two arguments to `Migration.build` that we didn't specify here: one [longevity
config](../context/config.html) for both the initial and final model. If we leave these unspecified,
they will be loaded via `com.typesafe.config.ConfigFactory.load()`. You don't have to worry about
setting `longevity.modelVersion` correctly in these configurations; they will be overridden by the
values you provide for the initial and final versions. This means it is generally the right thing
to do to use the same configuration you provide to your `LongevityContext` in your application.

Every persistent type in both your initial and final domain models must be covered by one of the
update steps you provide. You can confirm this by calling `migration.validate`, which will return a
`longevity.migrations.ValidationResult`, which summarizes the result of the validation process.
`ValidationResult.isValid` will return `true` whenever the migration is valid. Running the
migration, as described in the following section, will always validate a migration before applying
it.

{% assign prevTitle = "tagging a version of your domain model" %}
{% assign prevLink  = "tagging.html" %}
{% assign upTitle   = "migrating to a new version of your domain" %}
{% assign upLink    = "." %}
{% assign nextTitle = "running your migration" %}
{% assign nextLink  = "running.html" %}
{% include navigate.html %}
