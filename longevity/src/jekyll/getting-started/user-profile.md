---
title: the user profile
layout: page
---

Here is what our user profile looks like in Scala:

```scala
package simbl.domain

import longevity.model.annotations.component

@component[SimblDomainModel]
case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)
```

In longevity, we call the `UserProfile` a _persistent component_ - a
class that we persist, but not in its own table. They only get
persisted along with a containing persistent object such as `User`.

The `UserProfile` has two members that are also components: `Uri`
and `Markdown`:

```scala
package simbl.domain

import longevity.model.annotations.component

@component[SimblDomainModel]
case class Uri(uri: String)

@component[SimblDomainModel]
case class Markdown(markdown: String)
```

`Uri` and `Markdown` are simple wrapper classes for strings, which
provide extra type safety, but are also places where we might add some
extra functionality in the future. For instance, the `Uri` constructor
might throw some kind of validation exception if the provided string
is not a valid URI. As you can see, we can freely nest persistent
components within our persistent objects.

{% assign prevTitle = "building the user aggregate" %}
{% assign prevLink  = "user.html" %}
{% assign upTitle   = "getting started guide" %}
{% assign upLink    = "." %}
{% assign nextTitle = "username and email" %}
{% assign nextLink  = "keyvals.html" %}
{% include navigate.html %}
