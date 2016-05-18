---
title: testing your subdomain
layout: page
---

Longevity provides many tools for you to use in testing. For starters,
there are in-memory versions of all your repositories. We also provide
tools for you to do database integration tests - that is, tests that
exercise the repository against a real database. The `RepoCrudSpec`
tests basic CRUD operations for all your aggregates out of the
box. And you can subclass `QuerySpec` to easily exercise any queries
you rely on.

- [In Memory Repositories](in-mem-repos.html)
- [RepoCrudSpec](repo-crud-spec.html)
- [QuerySpec](query-spec.html)

<div class = "blue-side-bar">

<code>RepoCrudSpec</code> and <code>QuerySpec</code> are both <a href
= "http://www.scalatest.org/">ScalaTest</a> fixtures. While we <a href
= "https://www.pivotaltracker.com/story/show/114985815">plan to
write</a> equivalents for <a href =
"https://etorreborre.github.io/specs2/">specs2</a>, it is currently not
very high on our priority list. Please let us know if you would like
to see these, and we will adjust the priority.

</div>

{% assign prevTitle = "polymorphic repositories" %}
{% assign prevLink = "../repo/poly.html" %}
{% assign upTitle = "user manual" %}
{% assign upLink = ".." %}
{% assign nextTitle = "in memory repositories" %}
{% assign nextLink = "in-mem-repos.html" %}
{% include navigate.html %}
