package longevity.persistence

import emblem.TypeKey
import emblem.TypeKeyMap
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** a collection of repositories */
class RepoPool private[persistence](
  private[longevity] val baseRepoMap: TypeKeyMap[Any, PRepo],
  private[this] val schemaCreator: SchemaCreator) {

  /** a type key map for [[Repo repositories]]
   * @see emblem.TypeKeyMap
   */
  private val typeKeyMap: TypeKeyMap[Any, OldRepo] = baseRepoMap.widen

  /** select a repository by the type of persistent object */
  def apply[P : TypeKey]: OldRepo[P] = typeKeyMap[P]

  /** an iterable collection of the repositories */
  def values: collection.Iterable[OldRepo[_]] = typeKeyMap.values

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
    keyedPs: PWithTypeKey[_]*)(
    implicit executionContext: ExecutionContext)
  : Future[Seq[PState[_]]] = {
    val fpStates = keyedPs.map { keyedP =>
      def fpState[P](keyedP: PWithTypeKey[P]): Future[PState[_]] =
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
      def isPolyRepo(repo: PRepo[_]) = repo.isInstanceOf[BasePolyRepo[_]]
      def createSchemas(repoTest: (PRepo[_]) => Boolean) =
        Future.sequence(baseRepoMap.values.filter(repoTest).map(_.createSchema()))
      for {
        units1 <- createSchemas(isPolyRepo)
        units2 <- createSchemas(repo => !isPolyRepo(repo))
      } yield ()
    }

}
