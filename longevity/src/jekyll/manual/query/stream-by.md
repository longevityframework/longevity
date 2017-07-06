---
title: stream by query
layout: page
---

The `queryToIterator` and `queryToVector` methods we looked at in the [previous
section](retrieve-by.html) leave much to be desired. A streaming approach would be better, but which
streaming library should we choose? Scala has many. Currently, longevity supports four of them:

- [Akka Streams](http://doc.akka.io/docs/akka/2.5.2/scala/stream/index.html)
- [FS2](https://github.com/functional-streams-for-scala/fs2)
- [iteratee.io](https://github.com/travisbrown/iteratee)
- [Play enumerators](https://www.playframework.com/documentation/2.5.x/Enumerators)

Here's an example that uses all four:

```scala
import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.Eval
import fs2.Stream
import fs2.Task
import io.iteratee.{ Enumerator => CatsEnumerator }
import longevity.persistence.PState
import longevity.persistence.Repo
import play.api.libs.iteratee.{ Enumerator => PlayEnumerator }

val repo: Repo[SomeEffect, DomainModel] = longevityContext.repo

val blog: Blog = getBlogFromSomewhere()

val query: Query[BlogPost] = {
  import com.github.nscala_time.time.Imports._
  import BlogPost.queryDsl._
  import BlogPost.props._
  blogUri eqs blog.blogUri and postDate gt DateTime.now - 1.week
}

val akkaSource: SomeEffect[Source[PState[BlogPost], NotUsed]] =
  repo.queryToAkkaStream(query)

val fs2Stream: SomeEffect[Stream[Task, PState[P]]] =
  repo.queryToFS2(query)

val catsEnumerator: SomeEffect[CatsEnumerator[Eval, PState[P]]] =
  repo.queryToIterateeIo[Eval](query)

val playEnumerator: SomeEffect[PlayEnumerator[PState[P]]] = {
  import scala.concurrent.ExecutionContext.Implicits.global
  repo.queryToPlay(query)
}
```

You might wonder why all the streams are wrapped in our [effect](../context/effects.html) class.
After all, the are all streams, and are designed to handle side-effects themselves. The reason is
that these `Repo` methods have to behave well with the other `Repo` methods. For instance, consider
the following example:

```scala
def processStream(stream: Source[PState[BlogPost], NotUsed]): SomeEffect[Result] = {
  // ...
}

for {
  _      <- repo.openConnection
  stream <- repo.queryToAkkaStream(query)
  result <- processStream(stream)
  _      <- repo.closeConnection
} yield result
```

If the stream was obtained outside the effect, then the connection to the database would probably be
closed at the time we tried to process the stream.

All the streaming methods will require you to [supply
artifacts](../prelims/project-setup.html#supplying-optional-dependencies) for the streaming
libraries in your own build.

{% assign prevTitle = "retrieval by query" %}
{% assign prevLink  = "retrieve-by.html" %}
{% assign upTitle   = "queries" %}
{% assign upLink    = "." %}
{% assign nextTitle = "cassandra query limitations" %}
{% assign nextLink  = "cassandra-query-limits.html" %}
{% include navigate.html %}
