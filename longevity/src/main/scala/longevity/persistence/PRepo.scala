package longevity.persistence

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.Monad
import typekey.TypeKey
import fs2.Stream
import fs2.Task
import io.iteratee.{ Enumerator => CatsEnumerator }
import longevity.exceptions.persistence.UnstablePrimaryKeyException
import longevity.effect.Effect
import longevity.model.ModelType
import longevity.model.PType
import longevity.model.ptype.Key
import longevity.model.query.Query
import longevity.model.realized.RealizedPType
import play.api.libs.iteratee.{ Enumerator => PlayEnumerator }
import scala.concurrent.ExecutionContext
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
private[longevity] abstract class PRepo[F[_], M, P] private[persistence] (
  protected val effect: Effect[F],
  protected val modelType: ModelType[M],
  protected val pType: PType[M, P]) {

  private[persistence] var _repoOption: Option[Repo[F, M]] = None

  /** the pool of all the repos for the [[longevity.context.PersistenceContext]] */
  protected lazy val repo: Repo[F, M] = _repoOption.get

  protected[longevity] val realizedPType: RealizedPType[M, P] = modelType.realizedPTypes(pType)

  /** the type key for the persistent entities this repository handles */
  protected[persistence] val pTypeKey: TypeKey[P] = pType.pTypeKey

  protected def hasPrimaryKey = realizedPType.primaryKey.nonEmpty

  def create(unpersisted: P): F[PState[P]]

  def retrieve[V : Key[M, P, ?]](keyVal: V): F[Option[PState[P]]]

  def retrieveOne[V : Key[M, P, ?]](keyVal: V): F[PState[P]] = effect.map(retrieve(keyVal))(_.get)

  def update(state: PState[P]): F[PState[P]]

  def delete(state: PState[P]): F[Deleted[P]]

  protected def queryToChunkerator(query: Query[P]): Chunkerator[PState[P]]

  def queryToIterator(query: Query[P]): F[Iterator[PState[P]]] =
    effect.map(effect.pure(query))(q => queryToChunkerator(q).toIterator)

  def queryToVector(query: Query[P]): F[Vector[PState[P]]] =
    effect.mapBlocking(effect.pure(query))(q => queryToChunkerator(q).toVector)

  def queryToAkkaStreamImpl(query: Query[P]): F[Source[PState[P], NotUsed]] =
    effect.map(effect.pure(query))(q => chunkeratorToAkkaSource.adapt(queryToChunkerator(q)))

  def queryToFS2Impl(query: Query[P]): F[Stream[Task, PState[P]]] =
    effect.map(effect.pure(query))(q => chunkeratorToFS2Stream.adapt(queryToChunkerator(q)))

  def queryToIterateeIoImpl[F2[_]](query: Query[P])(implicit F2: Monad[F2]): F[CatsEnumerator[F2, PState[P]]] =
    effect.map(effect.pure(query))(q => chunkeratorToIterateeIoEnumerator.adapt(queryToChunkerator(q)))

  def queryToPlayImpl(query: Query[P])(implicit context: ExecutionContext): F[PlayEnumerator[PState[P]]] =
    effect.map(effect.pure(query))(q => chunkeratorToPlayEnumerator.adapt(queryToChunkerator(q)))

  protected[persistence] def createSchemaBlocking(): Unit

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
