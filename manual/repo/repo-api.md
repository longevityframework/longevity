---
title: repositories
layout: page
---

The API for a longevity repository provides basic CRUD persistence
operations, as follows:

```scala
package longevity.persistence

import akka.NotUsed
import akka.stream.scaladsl.Source
import longevity.subdomain.PRef
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Query
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** a repository for persistent objects of type `P` */
trait Repo[P <: Persistent] {

  /** creates the persistent object
   * 
   * @param unpersisted the persistent object to create
   * @param executionContext the execution context
   */
  def create(unpersisted: P)(implicit executionContext: ExecutionContext): Future[PState[P]]

  /** retrieves an optional persistent object from a persistent ref
   * 
   * @param ref the reference to use to look up the persistent object. this
   * could be a [[longevity.subdomain.ptype.KeyVal KeyVal]] or an
   * [[longevity.subdomain.Assoc Assoc]]
   * 
   * @param executionContext the execution context
   * 
   * @throws longevity.exceptions.persistence.AssocIsUnpersistedException
   * whenever the persistent ref is an unpersisted assoc
   */
  def retrieve(ref: PRef[P])(implicit executionContext: ExecutionContext): Future[Option[PState[P]]]

  /** retrieves a non-optional persistent object from a persistent ref
   * 
   * throws NoSuchElementException whenever the persistent ref does not refer
   * to a persistent object in the repository
   * 
   * @param ref the reference to use to look up the entity. this could be a
   * [[longevity.subdomain.ptype.KeyVal KeyVal]] or an
   * [[longevity.subdomain.Assoc Assoc]]
   *
   * @param executionContext the execution context
   * 
   * @throws longevity.exceptions.persistence.AssocIsUnpersistedException
   * whenever the persistent ref is an unpersisted assoc
   */
  def retrieveOne(ref: PRef[P])(implicit executionContext: ExecutionContext): Future[PState[P]]

  /** retrieves multiple persistent objects matching a query
   * 
   * @param query the query to execute
   * @param executionContext the execution context
   */
  def retrieveByQuery(query: Query[P])(implicit executionContext: ExecutionContext)
  : Future[Seq[PState[P]]]

  /** streams persistent objects matching a query
   * 
   * @param query the query to execute
   */
  def streamByQuery(query: Query[P]): Source[PState[P], NotUsed]

  /** updates the persistent object
   * 
   * @param state the persistent state of the persistent object to update
   * @param executionContext the execution context
   */
  def update(state: PState[P])(implicit executionContext: ExecutionContext): Future[PState[P]]

  /** deletes the persistent object
   * 
   * @param state the persistent state of the persistent object to delete
   * @param executionContext the execution context
   */
  def delete(state: PState[P])(implicit executionContext: ExecutionContext): Future[Deleted[P]]

}
```

Because all of the methods in `Repo` are potentially blocking, they
all return some kind of asynchronous construct - either a [Scala
`Future`](http://www.scala-lang.org/api/current/index.html#scala.concurrent.Future),
or, in the case of `streamByQuery`, an [Akka
Stream](http://doc.akka.io/docs/akka/current/scala/stream/index.html).

Methods returning a Scala `Future` require an implicit execution
context argument. The easiest way to provide this is to include
`import scala.concurrent.ExecutionContext.Implicits.global` at the top
of the file.

We will will discuss the `Repo` API methods in turn, but it's helpful
to cover a couple of points up front. First off, the `PRef` is a super
trait for both `Assoc` and `KeyVal`, so you can use the `retrieve` and
`retrieveOne` methods with both. Secondly, the `retrieveOne` method is
a simple wrapper method for `retrieve`, that opens up the
`Option[PState[R]]` for you. If the option is a `None`, this will
result in a `NoSuchElementException`.

<div class = "blue-side-bar">

There are two kinds of persistent refs: associations and key
values. we plan to integrate these two types more in the future. In
particular, it should be easier to embed a key value of another
aggregate in an entity, in place of embedding an association.

</div>

{% assign prevTitle = "repositories" %}
{% assign prevLink = "." %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo.create" %}
{% assign nextLink = "create.html" %}
{% include navigate.html %}
