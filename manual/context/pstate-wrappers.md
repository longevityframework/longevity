---
title: persistent state wrappers
layout: page
---

Longevity provides three type aliases, `FPState[P]`, `OPState[P]`, and
`FOPState[P]`, for types `Future[PState[P]]`, `Option[PState[P]]`, and
`Future[Option[PState[P]]]`, respectively. You can use these type
aliases as shorthands for the longer type names.

We also provide implicit classes that provide extra methods `mapP` and
`flatMapP` for each of the three types above. These methods will open
up the containing `Future`/`Option` combination, apply a function from
`P` to `P`, and wrap the result back up. The methods for `OPState[P]`
would look something like this:

```scala
class OPState[P <: Persistent] {

  /** map the optional PState by mapping the Persistent inside */
  def mapP(f: P => P): OPState[P]

  /** flatMap the optional PState by flat-mapping the Persistent inside */
  def flatMapP(f: P => Option[P]): OPState[P]

}
```

These convenience methods will save you the hassle of nested calls to
`map` for the multiple containers wrapping your `PState`. If you tend
to work with for comprehensions, you probably won't find them very
useful, except for perhaps `OPState`.

{% assign prevTitle = "persistent state" %}
{% assign prevLink = "persistent-state.html" %}
{% assign upTitle = "the longevity context" %}
{% assign upLink = "." %}
{% assign nextTitle = "repositories" %}
{% assign nextLink = "../repo" %}
{% include navigate.html %}
