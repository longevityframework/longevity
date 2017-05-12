package longevity.persistence

import emblem.typeKey
import emblem.TypeKey
import emblem.TypeKeyMap
import longevity.exceptions.persistence.NotInDomainModelTranslationException
import longevity.model.KeyVal
import longevity.model.query.Query
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** a repository for persistent objects in a model
 * 
 * @tparam M the model
 */
class Repo[M] private[persistence](
  private[persistence] val pRepoMap: TypeKeyMap[Any, PRepo],
  private[this] val schemaCreator: SchemaCreator) {

  /** non-desctructively creates any needed database constructs */
  def createSchema()(implicit context: ExecutionContext): Future[Unit] =
    schemaCreator.createSchema().flatMap { _ =>
      def isPolyRepo(repo: PRepo[_]) = repo.isInstanceOf[BasePolyRepo[_]]
      def createSchemas(repoTest: (PRepo[_]) => Boolean) =
        Future.sequence(pRepoMap.values.filter(repoTest).map(_.createSchema()))
      for {
        units1 <- createSchemas(isPolyRepo)
        units2 <- createSchemas(repo => !isPolyRepo(repo))
      } yield ()
    }

  /** creates the persistent object
   *
   * @param unpersisted the persistent object to create
   * @param executionContext the execution context
   */
  def create[P : TypeKey](unpersisted: P)(implicit executionContext: ExecutionContext): Future[PState[P]] = {
    pRepoMap.get[P] match {
      case Some(pr) => pr.create(unpersisted)
      case None => throw new NotInDomainModelTranslationException(typeKey[P].name)
    }
  }

  /** creates many persistent objects at once.
   *
   * because [[PWithTypeKey]] is an implicit class, you can call this method
   * using just aggregate roots, and the roots will be converted to
   * `PWithTypeKey` implicitly:
   *
   * {{{
   * repo.createMany(user1, user2, user2, blogPost1, blogPost2, blog)
   * }}}
   *
   * @param keyedPs the persistent objects to persist, wrapped with their
   * `TypeKeys`
   *
   * @param executionContext the execution context
   */
  def createMany(
    keyedPs: PWithTypeKey[_]*)(
    implicit executionContext: ExecutionContext)
  : Future[Seq[PState[_]]] = {
    def fpState[P](keyedP: PWithTypeKey[P]): Future[PState[_]] =
      create[P](keyedP.p)(keyedP.pTypeKey, executionContext)
    val fpStates = keyedPs.map(keyedP => fpState(keyedP))
    Future.sequence(fpStates)
  }

  /** retrieves an optional persistent object from a
   * [[longevity.model.KeyVal key value]]
   *
   * @tparam V the type of the key value
   * @param keyVal the key value to use to look up the persistent object
   * @param executionContext the execution context
   */
  def retrieve[P : TypeKey, V <: KeyVal[P] : TypeKey](keyVal: V)(implicit executionContext: ExecutionContext)
      : Future[Option[PState[P]]] =
    pRepoMap[P].retrieve[V](keyVal)

  /** retrieves an optional persistent object from a
   * [[longevity.model.KeyVal key value]]
   * 
   * throws NoSuchElementException whenever the persistent ref does not refer
   * to a persistent object in the repository
   * 
   * @tparam V the type of the key value
   * @param keyVal the key value to use to look up the persistent object
   * @param executionContext the execution context
   */
  def retrieveOne[P : TypeKey, V <: KeyVal[P] : TypeKey](keyVal: V)(implicit executionContext: ExecutionContext)
  : Future[PState[P]] =
    pRepoMap[P].retrieveOne[V](keyVal)

  /** retrieves multiple persistent objects matching a query
   * 
   * @param query the query to execute
   * @param executionContext the execution context
   */
  def queryToIterator[P : TypeKey](query: Query[P]): Iterator[PState[P]] = pRepoMap[P].queryToIterator(query)

  /** retrieves multiple persistent objects matching a query
   * 
   * @param query the query to execute
   * @param executionContext the execution context
   */
  def queryToFutureVec[P : TypeKey](query: Query[P])(implicit context: ExecutionContext)
      : Future[Vector[PState[P]]]
  = pRepoMap[P].queryToFutureVec(query)

  /** updates the persistent object
   * 
   * @param state the persistent state of the persistent object to update
   * @param executionContext the execution context
   */
  def update[P : TypeKey](state: PState[P])(implicit executionContext: ExecutionContext): Future[PState[P]] =
    pRepoMap[P].update(state)

  /** deletes the persistent object
   * 
   * @param state the persistent state of the persistent object to delete
   * @param executionContext the execution context
   */
  def delete[P : TypeKey](state: PState[P])(implicit executionContext: ExecutionContext): Future[Deleted[P]] =
    pRepoMap[P].delete(state)

  /** closes any open session from the underlying database */
  def closeSession()(implicit executionContext: ExecutionContext): Future[Unit] =
    pRepoMap.values.headOption.map(_.close()).getOrElse(Future.successful(()))

}
