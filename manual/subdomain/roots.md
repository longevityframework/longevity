---
title: aggregate roots
layout: page
---

We're building a blogging application, and our earliest user stories
to implement revolve around creating and setting up user accounts. The
first part of our domain that we want to flesh out is the User
aggregate. We start out by giving the user three basic fields:
`username`, `firstName`, and `lastName`. When we create our aggregate
root, we need to mark it as a `Root`:

{% gist sullivan-/db1226b4d31a0526ac8c %}

Every root entity class needs a corresponding `RootType`
instance. By convention, we designate the companion object as the root
entity type. We put all your entity types into an `EntityTypePool`,
and pass it to the subdomain:

{% gist sullivan-/6a68ac5f6f6331274e21 %}

All we need to do now is to [slap our `Subdomain` into a
`LongevityContext`](../context), and we are ready to start persisting users, as we
will see in a later chapter. TODO

{% assign prevTitle = "kinds of subdomains" %}
{% assign prevLink = "kinds.html" %}
{% assign upTitle = "building your subdomain" %}
{% assign upLink = "." %}
{% assign nextTitle = "basic properties" %}
{% assign nextLink = "basics.html" %}
{% include navigate.html %}

