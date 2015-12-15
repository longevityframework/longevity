---
title: using associations
layout: page
---

It's important to take note of the difference between a
_compositional_ relationship, in which we nest one entity inside
another:

{% gist sullivan-/2746cddb8821b6edaa7e %}

and an _assocational_ relationship, where one entity _indirectly_
references another. In the former case, "following" the relationship
is a trivial activity:

    val profile: UserProfile = user.profile.get

But crossing an aggregate boundary is a persistence operation, as
every aggregate lives within its own transactional scope. It is easy
enough to follow an association, but the entity you get back is
wrapped in its _persistent state_, which in turn is wrapped in a
`Future`, as retrieving the aggregate is commonly a blocking
operation:

    // blogPost.blog is an Assoc[Blog]
    val blog: Future[PState[Blog]] = blogPost.blog.retrieve

More information on retrieving an association can be found TODO.

When you are building up new, unpersisted entities, you can initialize
the associations directly:

    val post = BlogPost(
      uriSuffix = "2015/10/rev-your-engines.html",
      blog = Assoc(blog),
      authors = Set(Assoc(author1), Assoc(author2)))

When using your repositories to persist the new entities, you need not
worry about the order that you persist them in. If something hasn't
been persisted yet, it is recursively persisted.

Information on persisting new entities is here TODO.

{% assign prevTitle = "associations" %}
{% assign prevLink = "associations.html" %}
{% assign upTitle = "building your subdomain" %}
{% assign upLink = "." %}
{% assign nextTitle = "the root type" %}
{% assign nextLink = "../root-type" %}
{% include navigate.html %}
