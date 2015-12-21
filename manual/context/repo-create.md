---
title: repo.create
layout: page
---

`Repo.create` takes an unpersisted aggregate as argument, persists it,
and returns the persistent state:

    def create(unpersisted: R): Future[PState[R]]

When creating networks of aggregates together, you can use
`Assoc.apply` to build associations between them:

{% gist sullivan-/c2d126c9d2a83e76cfbc %}

In this situation, so long as you locate your activity to a single
thread, you do not need to worry about which one gets persisted
first. The `Repo` will work out which of the aggregates have already
been persisted, and recursively create any associated aggregate that
has not yet been persisted. So the order you call create on your
aggregates does not matter:

{% gist sullivan-/6264b96753b439108389 %}

Of course, this comes at a cost: the `Repo` is building up a cache of
newly created aggregates over time. This cache is kept thread-local,
so if you know your thread is going to terminate, you needn't worry
about it. But if the thread is going to be long-lived, you will want
to call `Repo.flush` from time to time. For instance, loading data
from a file:

{% gist sullivan-/3025144a5184e21c5992 %}

You can also freely associate with persisted entities when creating
new aggregates:

{% gist sullivan-/53384c60c432919b8776 %}

`Repo.create` gives back a `PState`, which you can in turn manipulate
and pass to `Repo.update` and `Repo.delete`.

TODO links here

TODO make this stuff actually work as advertised

{% assign prevTitle = "repositories" %}
{% assign prevLink = "repositories.html" %}
{% assign upTitle = "the longevity context" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo.retrieve" %}
{% assign nextLink = "repo-retrieve.html" %}
{% include navigate.html %}
