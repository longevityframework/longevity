---
title: entities
layout: page
---

Let's add a couple non-root entities to our user aggregate. Let's say
we want to give blog users the option to put up a profile page, where
they can put up a picture, a tagline, and a description in
[Markdown](https://en.wikipedia.org/wiki/Markdown). A lot of
information is stored within the user aggregate, so we want to keep
things organized and put the profile in a separate entity. We define
it in longevity like so:

{% gist sullivan-/62a216ece7a16bec63c9 %}

Let's add the profile to the user. They may not have created their
profile yet, so it should be optional:

{% gist sullivan-/ca7bb9e6911ff93b4743 %}

You need to add all your new entities into the `EntityTypePool`:

{% gist sullivan-/5b350f2f51ee61efcf8e %}

You can put entities in entities, and entities into [supported
collection types](collections.html) `Option`, `Set` and `List`,
collections into entities, use shorthands freely, etc. For example:

{% gist sullivan-/497fb4aa4393b2f1b0c3 %}

{% assign prevTitle = "shorthand pools" %}
{% assign prevLink = "shorthand-pools.html" %}
{% assign upTitle = "building your subdomain" %}
{% assign upLink = "." %}
{% assign nextTitle = "entities and value objects" %}
{% assign nextLink = "value-objects.html" %}
{% include navigate.html %}

