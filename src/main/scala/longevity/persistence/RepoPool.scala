package longevity.persistence

import emblem.TypeKey
import emblem.TypeKeyMap
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** a collection of repositories */
class RepoPool(
  private[longevity] val baseRepoMap: TypeKeyMap[Persistent, BaseRepo],
  private[this] val schemaCreator: SchemaCreator) {

  /** a `TypeKeyMap` of [[longevity.subdomain.persistent.Persistent]] to [[Repo]] */
  private val typeKeyMap: TypeKeyMap[Persistent, Repo] = baseRepoMap.widen

  /** select a repository by the type of persistent object */
  def apply[P <: Persistent : TypeKey]: Repo[P] = typeKeyMap[P]

  /** an iterable collection of the repositories */
  def values: collection.Iterable[Repo[_ <: Persistent]] = typeKeyMap.values

  /** creates many persistent objects at once.
   *
   * because [[PWithTypeKey]] is an implicit class, you can call this method
   * using just aggregate roots, and the roots will be converted to
   * `PWithTypeKey` implicitly:
   *
   * {{{
   * repoPool.createMany(user1, user2, user2, blogPost1, blogPost2, blog)
   * }}}
   *
   * @param keyedPs the persistent objects to persist, wrapped with their
   * `TypeKeys`
   *
   * @param executionContext the execution context
   * 
   * @see [Assoc.apply]
   */
  def createMany(
    keyedPs: PWithTypeKey[_ <: Persistent]*)(
    implicit executionContext: ExecutionContext)
  : Future[Seq[PState[_ <: Persistent]]] = {
    val fpStates = keyedPs.map { keyedP =>
      def fpState[P <: Persistent](keyedP: PWithTypeKey[P]): Future[PState[_ <: Persistent]] =
        typeKeyMap(keyedP.pTypeKey).create(keyedP.p)
      fpState(keyedP)
    }
    Future.sequence(fpStates)
  }

  /** closes any open session from the underlying database */
  def closeSession()(implicit executionContext: ExecutionContext): Future[Unit] =
    baseRepoMap.values.headOption.map(_.close()).getOrElse(Future.successful(()))

  /** non-desctructively creates any needed database constructs */
  def createSchema()(implicit context: ExecutionContext): Future[Unit] =
    schemaCreator.createSchema().flatMap { _ =>
      def isPolyRepo(repo: BaseRepo[_ <: Persistent]) = repo.isInstanceOf[BasePolyRepo[_]]
      def createSchemas(repoTest: (BaseRepo[_ <: Persistent]) => Boolean) =
        Future.sequence(baseRepoMap.values.filter(repoTest).map(_.createSchema()))
      for {
        units1 <- createSchemas(isPolyRepo)
        units2 <- createSchemas(repo => !isPolyRepo(repo))
      } yield ()
    }

}
