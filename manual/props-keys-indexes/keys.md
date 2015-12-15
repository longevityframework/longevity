---
title: natural keys
layout: page
---

Part of the longevity philosophy is to encapsulate all persistence
concerns within a persistence layer that provides a simple, intuitive
API. One upshot of this is that, within your application, you do not
have access to your database primary key. This may seem odd for those
of us that are used to using the database ID as an identifier
throughout the application. Our philosophy is that if the entity has a
natural key _as part of the domain model_, then we should include it
within our domain model. There is no reason
why our natural key should
be the same as our database ID.
(True, there are potential
performance concerns here, but at the moment, we are inclined to believe
they are negligible. We will investigate this more carefully when we have
the chance. Here's [the ticket](https://www.pivotaltracker.com/story/show/106611128).)
Because of this, we always keep the
two concepts separate in longevity.

Thankfully, it is a simple matter to define a natural key in
longevity. We just declare it in our `RootEntityType`, like so:

{% gist sullivan-/e2ef663857157a03a301 %}

We can declare multiple keys, and composite keys, just as
easily. Here, for instance, we add an ill-advised composite key on a
`firstName`/`lastName` combination:

{% gist sullivan-/b72900a6882b557e6728 %}

Without a natural key, you will not be able to retrieve individual
entities from the persistence layer, so you are most likely going to
want to define at least one. It is possible that you have an entity
type - perhaps representing an entry in a log file - for which there
are no natural keys. You may be satisfied to confine yourself to
looking up collections of these entities via queries such as range
searches. Unfortunately, aggregate queries is a feature that did not
make it into the longevity MMP release, but we can assure you that
this is a very high priority feature for us, and it will come
soon. All the same, if you are itching to use this feature, please let
us know! You can track the status of the feature on the [longevity
story board](https://www.pivotaltracker.com/story/show/100264584).

{% capture content %}

As you can see from the example, natural keys are built from
string-valued property names, and as such, an erroneous natural key
will not be detected until runtime. Any such errors will result in an
exception when the <code>Subdomain</code> is being built. We could
escalate these kinds of errors to compile time errors using <a href =
"http://scalamacros.org/">Scala macros</a>. We chose not to do this at
this time for a number of reasons, but mainly because, the current API
is "good enough" for now, and there are higher priority features to
implement.

{% endcapture %}
{% include longevity-meta.html content=content %}

{% assign prevTitle = "aggregate roots" %}
{% assign prevLink = "roots.html" %}
{% assign upTitle = "building your subdomain" %}
{% assign upLink = "." %}
{% assign nextTitle = "basic properties" %}
{% assign nextLink = "basics.html" %}
{% include navigate.html %}

