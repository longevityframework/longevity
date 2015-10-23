---
title: natural keys
layout: page
---

Part of the longevity philosophy is to encapsulate all persistence
concerns within a persistence layer that provides a simple, intuitive
API. One upshot of this is that, within your `LongevityContext`, you
do not have access to your database primary key. This may seem odd for
those of us that are used to using the database ID as an identifier
throughout the application. Our philosophy is that if the entity has a
natural key _as part of the domain model_, then we should include it
within our domain model. There is no reason - aside from potential
performance concerns of negligible merit - why our natural key should
be the same as our database ID. Because of this, we always keep the
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
this time for a number of reasons. First, the current API is "good
enough", and there are higher priority features to implement. Second,
it would be really nice if we could hold off on implementing macros until
<a href = "http://scalameta.org/">scala.meta</a> is ready for use. And
third, we would like to get a better sense of the usability of the
current API for natural keys before attempting it to write a macro for
it.

<br/><br/>

Even if we implement some macros for natural keys, we will probably
choose to leave in a runtime based implementation as well, for the
sake of any users that feel a little timid about using macros. If you
are itching for macro-driven natural keys, please let us know! You can
track the status of the feature on <a href =
"https://www.pivotaltracker.com/story/show/106521598">the longevity
story board</a>.

{% endcapture %}
{% include longevity-meta.html content=content %}

{% assign prevTitle = "aggregate roots" %}
{% assign prevLink = "roots.html" %}
{% assign upTitle = "building your subdomain" %}
{% assign upLink = "." %}
{% assign nextTitle="todo" %}
{% assign nextLink="todo.html" %}
{% include navigate.html %}

