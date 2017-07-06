---
title: choosing your effect
layout: page
---

Longevity will wrap the results of all your persistence operations in an "effect". The effect
describes how you would like longevity to handle the blocking nature of database operations. You can
choose to use [Scala futures](http://docs.scala-lang.org/overviews/core/futures.html), a more
functional, [IO
monad](http://underscore.io/blog/posts/2015/04/28/monadic-io-laziness-makes-you-free.html) kind of
approach, where all side-effects are pushed to the edges of your program, or an entirely synchronous
and blocking approach.

We already saw how to use futures in the previous section:

```scala
import longevity.context.LongevityContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

val context = LongevityContext[Future, DomainModel]()
```

You need to provide an execution context when creating a `LongevityContext` with a `Future` effect.
The easiest way to do this is to include `import scala.concurrent.ExecutionContext.Implicits.global`
at the top of your file.

For a more functional approach, you can use the
[cats-effect](https://github.com/typelevel/cats-effect) IO monad:


```scala
import cats.effect.IO
import longevity.context.LongevityContext
import longevity.effect.cats.ioEffect
import scala.concurrent.ExecutionContext.Implicits.global

val context = LongevityContext[IO, DomainModel]()
```

Using `IO` as an effect also requires an implicit execution context. This execution context is used
for synchronous operations. Blocking operations use a separate execution context, where blocking
threads are spun up as needed to handle the asynchony. In the code example above, we choose a
suitable default execution context for blocking operations. If you wish to explore using your own
blocking execution context, take a look at the [API for
longevity.effect.cats.ioEffect](../../api/longevity/effect/cats$.html#ioEffect). For an explanation
of the reason for using two execution contexts here, please see the "Thread Shifting" section in
this [IO monad for cats](http://typelevel.org/blog/2017/05/02/io-monad-for-cats.html) blog post.

If you want a synchronous persistence API, use `longevity.effect.Blocking`:

```scala
import longevity.context.LongevityContext
import longevity.effect.Blocking

val context = LongevityContext[Blocking, DomainModel]()
```

The type `Blocking[A]` is equivalent to `A`, similar to an `Id` monad like `cats.Id`. Because of
this, the results of persistence operations are unwrapped.

Many more effects are possible, and we intend to provide support for some of the more common
`Task`-like classes in the near future. But you can always provide your own. Simply create your own
`Effect` class by implementing [this API](../../api/longevity/effect/Effect.html), and make your
effect implicitly available when constructing your `LongevityContext`.

Some of the examples in this user manual may assume a `Future` effect, but you can always intuit how
things would look with other effects. For instance, if you see a code example like this:

```scala
val createResult: Future[PState[User]] = repo.create(user)
```

You can assume that it would look like this with the `IO` effect:

```scala
val createResult: IO[PState[User]] = repo.create(user)
```

Or like this with the `Blocking` effect:

```scala
val createResult: PState[User] = repo.create(user)
```

The [longevity demo project](https://github.com/longevityframework/demo) has multiple examples using
different effects.

{% assign prevTitle = "the longevity context" %}
{% assign prevLink  = "." %}
{% assign upTitle   = "the longevity context" %}
{% assign upLink    = "." %}
{% assign nextTitle = "configuring your longevity context" %}
{% assign nextLink  = "config.html" %}
{% include navigate.html %}
