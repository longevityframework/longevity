---
title: stream by query
layout: page
---

The `retrieveByQuery` method we just looked at returns a
`Future[Seq[PState[P]]]`, which may be convenient in some situations,
but will certainly not do in a reactive setting, or when your query
result is too large to store in memory all at once. For most purposes,
it is probably better to use `streamByQuery` instead. This method
returns an `akka.stream.scaladsl.Source`:

```scala
import akka.NotUsed
import akka.stream.scaladsl.Source
import longevity.persistence.PState

val blogState: PState[Blog] = ???

val recentPosts: Source[PState[BlogPost], NotUsed] = blogPostRepo.streamByQuery {
  import com.github.nscala_time.time.Imports._
  import BlogPost.queryDsl._
  import BlogPost.props._
  blog eqs blogState.assoc and postDate gt DateTime.now - 1.week
}
```

A `Source` is an input stream: a stream of things for you to
consume. In order to consume them, you need to put the `Source` into a
[flow](http://doc.akka.io/docs/akka/2.4.6/scala/stream/stream-flows-and-basics.html)
or
[graph](http://doc.akka.io/docs/akka/2.4.6/scala/stream/stream-graphs.html). For
instance, if you would like to print each blog post to the console as
it comes through:

```scala
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

implicit val system = ActorSystem("blogging")
implicit val materializer = ActorMaterializer()

recentPosts.runForeach { blogPostState =>
  println(s"query returned ${blogPostState.get}")
}
```

Note that you need an `ActorMaterializer` to run the stream. And you
need an `ActorSystem` to get the `ActorMaterializer`.

The above `runForeach` example is syntactic sugar for attaching a
`Sink` that does the `println`:

```scala
import akka.stream.scaladsl.Sink

recentPosts.to(Sink.foreach { state =>
  println(s"query returned ${state.get}")
})
```

The `Source` produces the `PStates`; the `Sink` consumes them.

You can easily map a stream of `PState[BlogPost]` into a stream  of
`BlogPost`:

```scala
recentPosts.map(_.get).runForeach {
  post: BlogPost => println(s"query returned ${post}")
}
```

Or count the number of items in the stream:

```scala
import akka.stream.scaladsl.Keep
import scala.concurrent.Future

val numRecentPosts: Future[Int] =
  recentPosts.map(_ => 1).toMat(Sink.reduce[Int](_ + _))(Keep.right).run()
```

Akka Streams are extremely flexible, and the scala DSL is very fluid
and easy to use, once you understand what is going on. Please read the
documentation on [Akka
Streams](http://doc.akka.io/docs/akka/current/scala/stream/index.html)
for more information.

Akka Streams is an optional dependency in longevity, so you'll need to
declare the dependency in your own project to use the
`Repo.streamByQuery` method:

```scala
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.4.8"
```

{% assign prevTitle = "retrieval by query" %}
{% assign prevLink = "query.html" %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "cassandra query limitations" %}
{% assign nextLink = "cassandra-query-limits.html" %}
{% include navigate.html %}
