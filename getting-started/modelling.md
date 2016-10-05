---
title: modelling our subdomain
layout: page
---

We have four types in our domain model that we want to persist: users,
blogs, blog posts, and comments. The arrows in this diagram indicate
relationships between them: comments are made on blog posts,
blog posts are made in a blog, and blogs, blog posts and comments all
have authors:

<img src="domain-model.png">

For the purposes of this tutorial, we are going to focus in on the
user, which consists of two main parts: the user itself, and the user
profile:

<img src="user-aggregate.png">

{% assign prevTitle = "getting started guide" %}
{% assign prevLink = "index.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="building the user aggregate" %}
{% assign nextLink="user.html" %}
{% include navigate.html %}
