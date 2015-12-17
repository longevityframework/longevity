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

All we need to do now is to slap our `Subdomain` into a
`LongevityContext`, and we are ready to start persisting users, as we
will see in a later chapter.

TODO: link to chapter on building LongevityContext

<div class="blue-side-bar">

You may find it onerous to have to extend a longevity class in your
domain. In theory, we could remove this requirement entirely, but it
makes the typing work out a lot more cleanly. It's not terribly
harmful either, as both <code>Entity</code> and
<code>Root</code> are simply empty-bodied marker traits (as you
can see from the <a
href="http://longevityframework.github.io/longevity/scaladocs/longevity-latest/#longevity.subdomain.Root">scaladocs</a>).

</div>

{% assign prevTitle = "kinds of subdomains" %}
{% assign prevLink = "kinds.html" %}
{% assign upTitle = "building your subdomain" %}
{% assign upLink = "." %}
{% assign nextTitle = "basic properties" %}
{% assign nextLink = "basics.html" %}
{% include navigate.html %}

