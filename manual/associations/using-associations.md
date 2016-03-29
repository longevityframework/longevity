---
title: using associations
layout: page
---

It's important to take note of the difference between a
_compositional_ relationship, in which we nest one entity inside
another:

```scala
val user = User(
  username = "speed-racer",
  email = "fastfreddy223@freddy-bot-mail-links.com",
  profile = Some(
    UserProfile(
      tagline = "Never did come in last",
      imageUrl = "http://idleserver787.imageshop.us/4165942987/racer.jpg",
      description = """Fred "Fast Fred" Farley, a 27 year professional race car driver, provides driving tips and inspirational driving stories for aspiring racers.""")))
```

and an _assocational_ relationship, where one entity _indirectly_
references another. In the former case, "following" the relationship
is a trivial activity:

```scala
val profile: UserProfile = user.profile.get
```

But crossing an aggregate boundary is a persistence operation, as
every aggregate lives within its own transactional scope. It is easy
enough to follow an association, but the entity you get back is
wrapped in its _persistent state_, which in turn is wrapped in a
`Future`, as retrieving the aggregate is commonly a blocking
operation:

```scala
// blogPost.blog is an Assoc[Blog]
// blogRepo is a Repo[Blog]
val blog: Future[PState[Blog]] = blogRepo.retrieveOne(blogPost.blog)
```

More information on retrieving an association can be found
[here](../repo/retrieve-assoc.html).

When you are building up new, unpersisted entities, you can initialize
the associations directly:

```scala
val post = BlogPost(
  uriSuffix = "2015/10/rev-your-engines.html",
  blog = Assoc(blog),
  authors = Set(Assoc(author1), Assoc(author2)))
```

However, these kinds of associations, referred to as "unpersisted
assocs", will be rejected by methods in the [repository
API](../repo/repo-api.html). They can only be used for [creating
multiple aggregates at once](../repo/create-many.html).

{% assign prevTitle = "associations" %}
{% assign prevLink = "." %}
{% assign upTitle = "associations" %}
{% assign upLink = "." %}
{% assign nextTitle = "the persistent type" %}
{% assign nextLink = "../ptype" %}
{% include navigate.html %}
