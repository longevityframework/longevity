---
title: modelling our subdomain
layout: page
---

We have four [entity
aggregates](http://longevityframework.github.io/longevity/manual/ddd-basics/aggregates-and-entities.html)
in our domain model: users, blogs, blog posts, and comments. The
arrows in this diagram indicate relationships between aggregates:
comments are made on blog posts, blog posts are made in a blog, and
blogs, blog posts and comments all have authors:

<img src="domain-model.png">

For the purposes of this tutorial, we are going to focus in on
the user aggregate. This aggregate consists of two entities: the
user itself, and the user profile:

<img src="user-aggregate.png">

{% assign prevTitle = "getting started guide" %}
{% assign prevLink = "index.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="building the user aggregate" %}
{% assign nextLink="user.html" %}
{% include navigate.html %}
