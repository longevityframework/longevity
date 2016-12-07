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

trait UserVerification {
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
import longevity.subdomain.annotations.polyComponent
import longevity.subdomain.annotations.derivedComponent
import longevity.subdomain.annotations.persistent
import org.joda.time.DateTime

@polyComponent
trait UserVerification {
  val verificationDate: DateTime
}

@derivedComponent[UserVerification]
case class EmailVerification(
  email: Email,
  verificationDate: DateTime)
extends UserVerification

@derivedComponent[UserVerification]
case class SmsVerification(
  phoneNumber: PhoneNumber,
  verificationDate: DateTime)
extends UserVerification

@derivedComponent[UserVerification]
case class GoogleSignIn(
  email: Email,
  idToken: String,
  verificationDate: DateTime)
extends UserVerification

@persistent(keySet = emptyKeySet)
case class User(
  username: String,
  email: Email,
  verifications: List[UserVerification])
```

The non-annotation equivalent is as follows:


```scala
import longevity.subdomain.PolyCType
import longevity.subdomain.DerivedCType
import longevity.subdomain.PType
import org.joda.time.DateTime

trait UserVerification {
  val verificationDate: DateTime
}

object UserVerification extends PolyCType[UserVerification]

case class EmailVerification(
  email: Email,
  verificationDate: DateTime)
extends UserVerification

object EmailVerification extends DerivedCType[EmailVerification, UserVerification]

case class SmsVerification(
  phoneNumber: PhoneNumber,
  verificationDate: DateTime)
extends UserVerification

object SmsVerification extends DerivedCType[SmsVerification, UserVerification]

case class GoogleSignIn(
  email: Email,
  idToken: String,
  verificationDate: DateTime)
extends UserVerification

object GoogleSignIn extends DerivedCType[GoogleSignIn, UserVerification]

case class User(
  username: String,
  email: Email,
  verifications: List[UserVerification])

object User extends PType[User] {
  object props {
    // ...
  }
  val keySet = emptyKeySet
}
```

{% assign prevTitle = "subtype polymorphism" %}
{% assign prevLink = "." %}
{% assign upTitle = "subtype polymorphism" %}
{% assign upLink = "." %}
{% assign nextTitle = "polymorphic persistents" %}
{% assign nextLink = "persistents.html" %}
{% include navigate.html %}
