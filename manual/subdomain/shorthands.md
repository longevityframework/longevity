---
title: shorthands
layout: page
---

Sometimes, using raw [basic types](basics.html) such as `String` is
unsatisfactory. We'd like a little more specificity in our types, to
help our code self-document, and so that we don't, say, overwrite a
username with an email:

    user.copy(user.username = user.email) // can we make this illegal?

One common approach would be to write a simple case class wrapper for
the underlying value, such as:

    case class Email(email: String)

But we're concerned that this is going to make the
[BSON](http://bsonspec.org/) serializations of our aggregates look a
little awkward. We would rather see this:

    { "username": "sullivan-",
      "email": "sullivan-@foo.com"
    }

Than this:

    { "username": "sullivan-",
      "email": {
        "email": "sullivan-@foo.com"
      }
    }

Longevity will serialize your user to the simpler BSON format if you
use _shorthands_. Collect all your shorthands into a `ShorthandPool`,
and make the shorthand pool implicitly available. Your
`RootEntityTypes` will need to know about them. Here's an example:

{% gist sullivan-/d1a59a70bbfbcc1e0f78 %}

Don't forget to pass your `ShorthandPool` to the `Subdomain`!

Shorthands like `Email` are a natural place to put constraint
validations, such as the well-formedness of an email address. Please
see the chapter on enforcing constraints for more information.

TODO: provide line to enforcing constraints chapter

{% assign prevTitle = "collections" %}
{% assign prevLink = "collections.html" %}
{% assign upTitle = "building your subdomain" %}
{% assign upLink = "." %}
{% assign nextTitle="todo" %}
{% assign nextLink="todo.html" %}
{% include navigate.html %}

