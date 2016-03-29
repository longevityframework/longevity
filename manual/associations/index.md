---
title: associations
layout: page
---

So far, we have seen how to build up rich entity aggregates using
[basic types](basics.html), [shorthands](shorthands.html),
[collections](collections.html), and [entities](entities.html). But we
haven't yet seen how to establish relationships between
aggregates. We'll call these kinds relationships _associations_. Any
entity can make an association, but they can only refer to aggregate
roots. We differentiate _associations_ from _compositions_, in which
the right-hand side of the relationship is a non-root entity.

In longevity, we model associations with `Assocs`. As an example,
consider that we have expanded our domain model to include users,
blogs, and blog posts. A blog post is on a blog, and blog posts and
blogs both have authors:

```scala
import longevity.subdomain.Assoc
import longevity.subdomain.EntityTypePool
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class User(username: String) extends Root
    
object User extends RootType[User] {
  object keys {
  }
  object indexes {
  }
}

case class Blog(uri: String, authors: Set[Assoc[User]])
extends Root

object Blog extends RootType[Blog] {
  object keys {
  }
  object indexes {
  }
}

case class BlogPost(uri: String, blog: Assoc[Blog], authors: Set[Assoc[Blog]])
extends Root

object BlogPost extends RootType[BlogPost] {
  object keys {
  }
  object indexes {
  }
}

val subdomain = Subdomain("blogging", EntityTypePool(User, Blog, BlogPost))
```

Non-root entities can associate with other aggregates as well. For
instance, suppose we want to require every author to publish a
separate profile for every blog they are a member of. We can make the
`UserProfile` a member of the `Blog` aggregate, and put the
association to `User` there:

```scala
import longevity.subdomain.Assoc
import longevity.subdomain.Entity
import longevity.subdomain.EntityType
import longevity.subdomain.EntityTypePool
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class User(username: String) extends Root

object User extends RootType[User] {
  object keys {
  }
  object indexes {
  }
}

case class UserProfile(
  user: Assoc[User],
  tagline: String,
  imageUri: String,
  description: String)
extends Entity

object UserProfile extends EntityType[UserProfile]

case class Blog(uri: String, authors: Set[UserProfile])
extends Root

object Blog extends RootType[Blog] {
  object keys {
  }
  object indexes {
  }
}

val subdomain = Subdomain("blogging", EntityTypePool(User, UserProfile, Blog))
```

<div class="blue-side-bar">

Having <a href =
"http://aviadezra.blogspot.com/2009/05/uml-association-aggregation-composition.html">reviewed
our UML terminology</a> recently, it seems like a more appropriate
name for what we are calling an association would be
<i>aggregation</i>. But we will probably stick with
<code>Assoc</code>, because it's an easily recognizable term, and it's
the only kind of UML association that we explicitly model. We
typically use the term "relationship" to express the UML concept of
association.

</div>

{% assign prevTitle = "limitations" %}
{% assign prevLink = "../subdomain/limitations.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "using associations" %}
{% assign nextLink = "using-associations.html" %}
{% include navigate.html %}
