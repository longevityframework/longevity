package longevity.persistence

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.Monad
import emblem.TypeKey
import fs2.Stream
import fs2.Task
import io.iteratee.{ Enumerator => CatsEnumerator }
import longevity.exceptions.persistence.UnstablePrimaryKeyException
import longevity.model.KVEv
import longevity.model.ModelType
import longevity.model.PType
import longevity.model.query.Query
import longevity.model.realized.RealizedPType
import play.api.libs.iteratee.{ Enumerator => PlayEnumerator }
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking
import streamadapter.Chunkerator
import streamadapter.akka.chunkeratorToAkkaSource
import streamadapter.fs2.chunkeratorToFS2Stream
import streamadapter.iterateeio.chunkeratorToIterateeIoEnumerator
import streamadapter.play.chunkeratorToPlayEnumerator

/** an abstract base class for [[Repo]] implementations
 * 
 * @param pType the entity type for the persistent entities this repository handles
 * @param modelType the model type containing the persistent entities that this repo persists
 */
private[longevity] abstract class PRepo[M, P] private[persistence] (
  protected[longevity] val pType: PType[M, P],
  protected[longevity] val modelType: ModelType[M]) {

  private[persistence] var _repoOption: Option[Repo[M]] = None

  /** the pool of all the repos for the [[longevity.context.PersistenceContext]] */
  protected lazy val repo: Repo[M] = _repoOption.get

  protected[longevity] val realizedPType: RealizedPType[M, P] = modelType.realizedPTypes(pType)

  /** the type key for the persistent entities this repository handles */
  protected[persistence] val pTypeKey: TypeKey[P] = pType.pTypeKey

  protected def hasPrimaryKey = realizedPType.primaryKey.nonEmpty

  def create(unpersisted: P)(implicit executionContext: ExecutionContext): Future[PState[P]]

  def retrieve[V : KVEv[M, P, ?]](keyVal: V)(implicit executionContext: ExecutionContext)
  : Future[Option[PState[P]]]

  def retrieveOne[V : KVEv[M, P, ?]](keyVal: V)(implicit context: ExecutionContext): Future[PState[P]] =
    retrieve(keyVal).map(_.get)

  def update(state: PState[P])(implicit executionContext: ExecutionContext): Future[PState[P]]

  def delete(state: PState[P])(implicit executionContext: ExecutionContext): Future[Deleted[P]]

  protected def queryToChunkerator(query: Query[P]): Chunkerator[PState[P]]

  def queryToIterator(query: Query[P]): Iterator[PState[P]] = queryToChunkerator(query).toIterator

  def queryToFutureVec(query: Query[P])(implicit context: ExecutionContext): Future[Vector[PState[P]]] =
    Future(blocking(queryToChunkerator(query).toVector))

  def queryToAkkaStreamImpl(query: Query[P]): Source[PState[P], NotUsed] =
    chunkeratorToAkkaSource.adapt(queryToChunkerator(query))

  def queryToFS2Impl(query: Query[P]): Stream[Task, PState[P]] =
    chunkeratorToFS2Stream.adapt(queryToChunkerator(query))

  def queryToIterateeIoImpl[F[_]](query: Query[P])(implicit F: Monad[F]): CatsEnumerator[F, PState[P]] =
    chunkeratorToIterateeIoEnumerator.adapt(queryToChunkerator(query))

  def queryToPlayImpl(query: Query[P])(implicit context: ExecutionContext): PlayEnumerator[PState[P]] =
    chunkeratorToPlayEnumerator.adapt(queryToChunkerator(query))

  protected[persistence] def close()(implicit context: ExecutionContext): Future[Unit]

  protected[persistence] def createSchema()(implicit context: ExecutionContext): Future[Unit]

  protected def validateStablePrimaryKey(state: PState[P]): Unit = {
    realizedPType.primaryKey.map { key =>
      val origKeyVal = key.keyValForP(state.orig)
      val newKeyVal = key.keyValForP(state.get)
      if (origKeyVal != newKeyVal) {
        throw new UnstablePrimaryKeyException(state.orig, origKeyVal, newKeyVal)
      }
    }
  }

}
