---
title: username and email
layout: page
---

The final components of our user aggregate are `Username` and `Email`,
which are the key values for our natural keys, `User.keys.username`
and `User.keys.email`, respectively. Here's the code for `Username`:

```scala
package simbl.domain

import longevity.subdomain.KeyVal

case class Username(username: String)
extends KeyVal[User, Username](User.keys.username)
```

Aside from being parts of our user aggregate, we can also embed them
in other classes. For instance, `BlogPost` contains a `Set[Username]`
to indicate the authors of the post:

```scala
case class BlogPost(
  uri: BlogPostUri,
  title: String,
  slug: Option[Markdown] = None,
  content: Markdown,
  labels: Set[String] = Set(),
  postDate: DateTime,
  blog: BlogUri,
  authors: Set[Username])
extends Root
```

{% assign prevTitle = "the user profile" %}
{% assign prevLink = "user-profile.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="building the subdomain" %}
{% assign nextLink="building.html" %}
{% include navigate.html %}
