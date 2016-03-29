---
title: shorthands
layout: page
---

Sometimes, using raw [basic types](basics.html) such as `String` is
unsatisfactory. We'd like a little more specificity in our types, to
help our code self-document, and so that we don't, say, overwrite a
username with an email:

```scala
user.copy(user.username = user.email) // can we make this illegal?
```

One common approach is to write a simple case class wrapper for the
underlying value, such as:

```scala
case class Email(email: String)
```

But we're concerned that this is going to make the
[BSON](http://bsonspec.org/) serializations of our aggregates look a
little awkward. We would rather see this:

```json
{ "username": "sullivan",
  "email": "sullivan@foo.com"
}
```

than this:

```json
{ "username": "sullivan",
  "email": {
    "email": "sullivan@foo.com"
  }
}
```

Longevity will serialize your user to the simpler BSON format if you
use _shorthands_. A shorthand provides translations between your
domain type and an abbreviated type (`Email` and `String`, in this
example). The abbreviated type should be a [basic type](basics.html).

Collect all your shorthands into a `ShorthandPool`, and make the
shorthand pool implicitly available to your `RootTypes`, as they
will need to know about them. Here's an example:

```scala
import longevity.subdomain.EntityTypePool
import longevity.subdomain.Shorthand
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

object shorthands {
  case class Email(email: String)
  val emailShorthand = Shorthand[Email, String]
  implicit val shorthandPool = ShorthandPool(emailShorthand)
}

import shorthands._

case class User(
  username: String,
  firstName: String,
  lastName: String,
  primaryEmail: Email,
  emails: Set[Email])
extends Root

object User extends RootType[User] {
  object keys {
  }
  object indexes {
  }
}

val subdomain = Subdomain("blogging", EntityTypePool(User))
```

Note that you can nest shorthands inside of collections, as the above
example shows.

It can be useful to define implicit defs as well to convert from the
abbreviated to the actual, as used to construct a user here:

```scala
import longevity.subdomain.Shorthand
import longevity.subdomain.ShorthandPool

object shorthands {
  case class Email(email: String)
  implicit def toEmail(email: String) = Email(email)
  val emailShorthand = Shorthand[Email, String]
  implicit val shorthandPool = ShorthandPool(emailShorthand)
}

import shorthands._

val user = User(
  "bolt",
  "Jeremy",
  "Linden",
  "bolt26@info.com",
  Set("bolt26@info.com", "bolt65766@gmail.com"))
```

Shorthands like `Email` are a natural place to put constraint
validations, such as the well-formedness of an email address. Please
see the [chapter on enforcing constraints](../constraints.html) for
more information.

{% assign prevTitle = "collections" %}
{% assign prevLink = "../subdomain/collections.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "shorthand pools" %}
{% assign nextLink = "shorthand-pools.html" %}
{% include navigate.html %}
