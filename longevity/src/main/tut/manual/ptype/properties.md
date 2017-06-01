---
title: properties
layout: page
---

In our `PType`, when we talk about the fields of our persistent
object, we talk about properties, or `Props`. Properties map to
underlying members within the [persistent object](../persistent), at
any depth. They follow a path from the root of the persistent object,
and take on the type of that member in the persistent.

When we use the `@persistent` annotation, the properties are generated
for us automatically. For example, if we define `User` as follows:

```scala
import longevity.model.annotations.component
import longevity.model.annotations.persistent

@component case class Email(email: String)
@component case class Markdown(markdown: String)
@component case class Uri(uri: String)

@component
case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)

@persistent(keySet = emptyKeySet)
case class User(
  username: String,
  email: Email,
  profile: UserProfile)
```                          

Longevity will generate a `User` companion object that looks something
like this:

```scala
import longevity.model.PType
import longevity.model.ptype.Prop

object User extends PType[User] {
  object props {
    object username extends Prop[User, String]("username")
    object email extends Prop[User, Email]("email")
    object profile extends Prop[User, UserProfile]("profile") {
      object tagline extends Prop[User, String]("tagline")
      object imageUri extends Prop[User, Uri]("imageUri")
      object markdown extends Prop[User, Markdown]("markdown")
    }
  }
  lazy val keySet = emptyKeySet
}
```

So if we wanted to refer to the `markdown` on a `User's` profile, we
would say `User.props.profile.markdown`.

You can build the properties by hand if you like, but you will need to
specify both the path, and the type of the property yourself. Longevity
will check that the type is correct when the `DomainModel` is created.

If you are actually writing out your properties by hand, the following
style is much more compact:

```scala
import longevity.model.PType

object User extends PType[User] {
  object props {
    val username = prop[String]("username")
    val email = prop[Email]("email")
    // ...
  }
  lazy val keySet = emptyKeySet
}
```

In principle, properties could map through any path from the
persistent object, and have a wide variety of types. In practice, the
kinds of properties currently supported is somewhat limited. We do
plan to address all of these limitations, and removing some of them is
high priority. For more details, please see the ["remove restrictions
on properties" epic](https://www.pivotaltracker.com/epic/show/2975505)
on our story board. Here are there current limitations:

  - No properties with collection types.
  - No properties with types that (recursively) contain members with collection or polymorphic types.
  - No properties with paths that contain collections.
  - No properties with paths that terminate with a polymorphic type.

{% assign prevTitle = "the persistent type" %}
{% assign prevLink = "." %}
{% assign upTitle = "the persistent type" %}
{% assign upLink = "." %}
{% assign nextTitle = "keys" %}
{% assign nextLink = "keys.html" %}
{% include navigate.html %}

