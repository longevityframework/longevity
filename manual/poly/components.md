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
polymorphic type `UserVerification`, and its children. When building
our `Subdomain`, we define the `CType` of the parent as a `PolyCType`,
and that of the children as `DerivedCTypes`:

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

import longevity.subdomain.DerivedCType
import longevity.subdomain.CType
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool
import longevity.subdomain.PolyCType
import longevity.subdomain.Subdomain

val subdomain = Subdomain(
  "blogging",
  PTypePool(User),
  CTypePool(
    CType[Email],
    CType[PhoneNumber],
    PolyCType[UserVerification],
    DerivedCType[EmailVerification, UserVerification],
    DerivedCType[SmsVerification, UserVerification],
    DerivedCType[GoogleSignIn, UserVerification]))
```

{% assign prevTitle = "subtype polymorphism" %}
{% assign prevLink = "." %}
{% assign upTitle = "subtype polymorphism" %}
{% assign upLink = "." %}
{% assign nextTitle = "polymorphic persistents" %}
{% assign nextLink = "persistents.html" %}
{% include navigate.html %}

