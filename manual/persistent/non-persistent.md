---
title: persistent and non-persistent entities
layout: page
---

The `Persistent` trait is a specialization of trait `Entity`, which is
a generic domain class to use with longevity. A `Persistent` functions
as the "root" of an entity tree that you want to persist, and can have
other entities embedded within it. For instance, our application has
users that optionally have a user profile:

```scala
import longevity.subdomain.Entity
import longevity.subdomain.persistent.Root

case class User(
  username: String,
  email: Email,
  profile: Option[UserProfile])
extends Root

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)
extends Entity
```

In this example, our `UserProfile` will get persisted along with its
corresponding `User`. It wouldn't make sense to make `UserProfile` a
`Root`, or any other kind of `Persistent`, because they are not
persisted separately. They are only persisted as part of persisting a
user.

Aside from `Persisted`, `Entity` has one other child trait:
`ValueObject`. [Value objects](../entities/value-objects.html) are
functionally equivalent to an `Entity`. The entire hierarchy looks
like so:

- `Entity`
  - `ValueObject`
  - `Persistent`
    - `Root`
    - `Event`
    - `ViewItem`

<div class="blue-side-bar">

You may find it onerous to have to extend a longevity class in your
domain. In theory, we could remove this requirement entirely, but it
makes the typing work out a lot more cleanly. It's not terribly
harmful either, as every trait in the <code>Entity</code> hierarchy is
a simple empty-bodied marker trait (as you
can see from the <a
href="http://longevityframework.github.io/longevity/scaladocs/longevity-latest/#longevity.subdomain.Entity">scaladocs</a>).

</div>

{% assign prevTitle = "kinds of persistent objects" %}
{% assign prevLink = "kinds.html" %}
{% assign upTitle = "persistent objects" %}
{% assign upLink = "." %}
{% assign nextTitle = "persistent types" %}
{% assign nextLink = "ptypes.html" %}
{% include navigate.html %}

