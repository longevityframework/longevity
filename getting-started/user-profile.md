---
title: the user profile
layout: page
---

The user profile is an entity that is part of the user aggregate:

```scala
package simbl.domain

import longevity.subdomain.embeddable.Embeddable

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)
extends Embeddable
```

`UserProfile` a simple case class that extends trait `Embeddable`,
which we use to mark things that we want to embed in our persistent
objects.

The `UserProfile` has two members that are also `Embeddables`: `Uri`
and `Markdown`. They look pretty similar, so we'll only present `Uri`
here:

```scala
package simbl.domain

import longevity.subdomain.embeddable.Embeddable

case class Uri(uri: String) extends Embeddable
```

`Uri` and `Markdown` are simple wrapper classes for strings, which
provide extra type safety, but are also places where we might add some
extra functionality in the future. For instance, the `Uri` constructor
might throw some kind of validation exception if the provided string
is not a valid URI. As you can see, we can freely nest `Embeddables`
within our `Persistent` classes.

{% assign prevTitle = "building the user aggregate" %}
{% assign prevLink = "user.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="username and email" %}
{% assign nextLink="keyvals.html" %}
{% include navigate.html %}
