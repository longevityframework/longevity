---
title: polymorphic components
layout: page
---

Let's say that on our blogging site, we want to be able to verify the
identity of our users, and we have a few different approaches for
doing so. We can use _email verification_, where we send the user an
email, with a link that they can click to verify the email address. We
can also use _SMS verification_, where we send the user an SMS message
with a temporary code in it, and ask the user to enter it into the
webpage. Or we can use a third-party account verification system such
as Google Sign-In.

An idealized representation of this portion of our domain might look
like so:

```scala
import org.joda.time.DateTime

sealed trait UserVerification {
  val verificationDate: DateTime
}

case class EmailVerification(
  email: Email,
  verificationDate: DateTime)
extends UserVerification

case class SmsVerification(
  phoneNumber: PhoneNumber,
  verificationDate: DateTime)
extends UserVerification

case class GoogleSignIn(
  email: Email,
  idToken: String,
  verificationDate: DateTime)
extends UserVerification
```

In our `User` object, we want to keep track of all the user's
successful verification attempts:

```scala
case class User(
  username: String,
  email: Email,
  verifications: List[UserVerification])
```

For this to work, all we need to do is to make longevity aware of our
polymorphic component `UserVerification`, and its children. We do this
using annotations `@polyComponent` and `@derivedComponent`:

```scala
import longevity.model.annotations.polyComponent
import longevity.model.annotations.derivedComponent
import longevity.model.annotations.persistent
import org.joda.time.DateTime

@polyComponent[DomainModel]
sealed trait UserVerification {
  val verificationDate: DateTime
}

@derivedComponent[DomainModel, UserVerification]
case class EmailVerification(
  email: Email,
  verificationDate: DateTime)
extends UserVerification

@derivedComponent[DomainModel, UserVerification]
case class SmsVerification(
  phoneNumber: PhoneNumber,
  verificationDate: DateTime)
extends UserVerification

@derivedComponent[DomainModel, UserVerification]
case class GoogleSignIn(
  email: Email,
  idToken: String,
  verificationDate: DateTime)
extends UserVerification

@persistent[DomainModel]
case class User(
  username: String,
  email: Email,
  verifications: List[UserVerification])
```

Note that to satisfy the requirements for [shapeless](https://github.com/milessabin/shapeless), we
must form a proper [abstract data type](https://en.wikipedia.org/wiki/Abstract_data_type) by sealing
the `UserVerification` trait. This will require us to define all the subclasses in the same file.
If we forget to seal the trait, you will get an implicit resolution compiler error such as:

```
could not find implicit value for parameter arbitrary: org.scalacheck.Arbitrary[UserVerification]
```

The non-annotation equivalent is as follows:


```scala
import longevity.model.DerivedCType
import longevity.model.PType
import longevity.model.PolyCType
import org.joda.time.DateTime

sealed trait UserVerification {
  val verificationDate: DateTime
}

object UserVerification extends PolyCType[DomainModel, UserVerification]

case class EmailVerification(
  email: Email,
  verificationDate: DateTime)
extends UserVerification

object EmailVerification extends DerivedCType[DomainModel, EmailVerification, UserVerification]

case class SmsVerification(
  phoneNumber: PhoneNumber,
  verificationDate: DateTime)
extends UserVerification

object SmsVerification extends DerivedCType[DomainModel, SmsVerification, UserVerification]

case class GoogleSignIn(
  email: Email,
  idToken: String,
  verificationDate: DateTime)
extends UserVerification

object GoogleSignIn extends DerivedCType[DomainModel, GoogleSignIn, UserVerification]

case class User(
  username: String,
  email: Email,
  verifications: List[UserVerification])

object User extends PType[DomainModel, User] {
  object props {
    // ...
  }
}
```

{% assign prevTitle = "subtype polymorphism" %}
{% assign prevLink  = "." %}
{% assign upTitle   = "subtype polymorphism" %}
{% assign upLink    = "." %}
{% assign nextTitle = "polymorphic persistents" %}
{% assign nextLink  = "persistents.html" %}
{% include navigate.html %}

