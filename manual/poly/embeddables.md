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
import longevity.subdomain.persistent.Root

case class User(
  username: String,
  email: Email,
  verifications: List[UserVerification])
extends Root
```

For this to work, all we need to do is to make longevity aware of our
polymorphic type `UserVerification`, and its children. All four will
be embeddables, but we mark the `EType` of the parent as a `PolyType`,
and that of the children as `DerivedType`:

```scala
import longevity.subdomain.embeddable.DerivedType
import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.PolyType
import org.joda.time.DateTime

trait UserVerification extends Entity {
  val verificationDate: DateTime
}

object UserVerification extends PolyType[UserVerification]

case class EmailVerification(
  email: Email,
  verificationDate: DateTime)
extends UserVerification

object EmailVerification extends DerivedType[EmailVerification, UserVerification]

case class SmsVerification(
  phoneNumber: PhoneNumber,
  verificationDate: DateTime)
extends UserVerification

object SmsVerification extends DerivedType[SmsVerification, UserVerification]

case class GoogleSignIn(
  email: Email,
  idToken: String,
  verificationDate: DateTime)
extends UserVerification

object GoogleSignIn extends DerivedType[GoogleSignIn, UserVerification]

import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.entity.EntityTypePool
import longevity.subdomain.ptype.PTypePool

val subdomain = Subdomain(
  PTypePool(User),
  EntityTypePool(UserVerification, EmailVerification, SmsVerification, GoogleSignIn),
  ShorthandPool(Email, PhoneNumber))
```

{% assign prevTitle = "subtype polymorphism" %}
{% assign prevLink = "." %}
{% assign upTitle = "subtype polymorphism" %}
{% assign upLink = "." %}
{% assign nextTitle = "polymorphic persistents" %}
{% assign nextLink = "persistents.html" %}
{% include navigate.html %}

