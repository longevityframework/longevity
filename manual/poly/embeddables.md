---
title: polymorphic embeddables
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

In our `User` aggregate, we want to keep track of all the user's
successful verification attempts:

```scala
import longevity.subdomain.Persistent

case class User(
  username: String,
  email: Email,
  verifications: List[UserVerification])
extends Persistent
```

For this to work, all we need to do is to make longevity aware of our
polymorphic type `UserVerification`, and its children. All four will
be embeddables, but we mark the `EType` of the parent as a `PolyEType`,
and that of the children as `DerivedEType`:

```scala
import longevity.subdomain.Embeddable
import org.joda.time.DateTime

trait UserVerification extends Embeddable {
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

import longevity.subdomain.DerivedEType
import longevity.subdomain.EType
import longevity.subdomain.ETypePool
import longevity.subdomain.PTypePool
import longevity.subdomain.PolyEType
import longevity.subdomain.Subdomain

val subdomain = Subdomain(
  "blogging",
  PTypePool(User),
  ETypePool(
    EType[Email],
    EType[PhoneNumber],
    PolyEType[UserVerification],
    DerivedEType[EmailVerification, UserVerification],
    DerivedEType[SmsVerification, UserVerification],
    DerivedEType[GoogleSignIn, UserVerification]))
```

{% assign prevTitle = "subtype polymorphism" %}
{% assign prevLink = "." %}
{% assign upTitle = "subtype polymorphism" %}
{% assign upLink = "." %}
{% assign nextTitle = "polymorphic persistents" %}
{% assign nextLink = "persistents.html" %}
{% include navigate.html %}

