package longevity.persistence

import emblem.TypeKey
import emblem.TypeKeyMap
import longevity.exceptions.persistence.NotInDomainModelTranslationException
import longevity.model.KeyVal
import longevity.model.PEv
import longevity.model.query.Query
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** a repository for persistent objects in a model
 * 
 * @tparam M the model
 */
abstract class Repo[M] private[persistence](private[this] val schemaCreator: SchemaCreator) {

  /** evidence for a persistent class where the model is fixed */
  type PEvM[P] = PEv[M, P]

  private[persistence] type PRepoM[P] = PRepo[M, P]

  private[persistence] val pRepoMap: TypeKeyMap[Any, PRepoM]

  /** non-desctructively creates any needed database constructs */
  def createSchema()(implicit context: ExecutionContext): Future[Unit] =
    schemaCreator.createSchema().flatMap { _ =>
      def isPolyRepo(repo: PRepo[M, _]) = repo.isInstanceOf[BasePolyRepo[M, _]]
      def createSchemas(repoTest: (PRepo[M, _]) => Boolean) =
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
  def create[P: PEvM](unpersisted: P)(implicit executionContext: ExecutionContext): Future[PState[P]] = {
    val key = implicitly[PEv[M, P]].key
    pRepoMap.get(key) match {
      case Some(pr) => pr.create(unpersisted)
      case None => throw new NotInDomainModelTranslationException(key.name)
    }
  }

  /** creates many persistent objects at once.
   *
   * because [[PWithEv]] is an implicit class, you can call this method
   * using just aggregate roots, and the roots will be converted to
   * `PWithEv` implicitly:
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
  def createMany(pWithEvs: PWithEv[M, _]*)(implicit executionContext: ExecutionContext)
  : Future[Seq[PState[_]]] = {
    def fpState[P](pWithEv: PWithEv[M, P]): Future[PState[_]] =
      create[P](pWithEv.p)(pWithEv.ev, executionContext)
    val fpStates = pWithEvs.map(pWithEv => fpState(pWithEv))
    Future.sequence(fpStates)
  }

  /** retrieves an optional persistent object from a [[longevity.model.KeyVal key value]]
   *
   * @tparam V the type of the key value
   * @param keyVal the key value to use to look up the persistent object
   * @param executionContext the execution context
   */
  def retrieve[P : PEvM, V <: KeyVal[P] : TypeKey](keyVal: V)(implicit executionContext: ExecutionContext)
  : Future[Option[PState[P]]] =
    pRepoMap(implicitly[PEv[M, P]].key).retrieve[V](keyVal)

  /** retrieves an optional persistent object from a [[longevity.model.KeyVal key value]]
   * 
   * throws NoSuchElementException whenever the persistent ref does not refer to a persistent object
   * in the repository
   * 
   * @tparam V the type of the key value
   * @param keyVal the key value to use to look up the persistent object
   * @param executionContext the execution context
   */
  def retrieveOne[P: PEvM, V <: KeyVal[P] : TypeKey](keyVal: V)(implicit executionContext: ExecutionContext)
  : Future[PState[P]] =
    pRepoMap(implicitly[PEv[M, P]].key).retrieveOne[V](keyVal)

  /** retrieves multiple persistent objects matching a query
   * 
   * @param query the query to execute
   * @param executionContext the execution context
   */
  def queryToIterator[P: PEvM](query: Query[P]): Iterator[PState[P]] =
    pRepoMap(implicitly[PEv[M, P]].key).queryToIterator(query)

  /** retrieves multiple persistent objects matching a query
   * 
   * @param query the query to execute
   * @param executionContext the execution context
   */
  def queryToFutureVec[P: PEvM](query: Query[P])(implicit context: ExecutionContext)
  : Future[Vector[PState[P]]]
  = pRepoMap(implicitly[PEv[M, P]].key).queryToFutureVec(query)

  /** updates the persistent object
   * 
   * @param state the persistent state of the persistent object to update
   * @param executionContext the execution context
   */
  def update[P : PEvM](state: PState[P])(implicit executionContext: ExecutionContext): Future[PState[P]] =
    pRepoMap(implicitly[PEv[M, P]].key).update(state)

  /** deletes the persistent object
   * 
   * @param state the persistent state of the persistent object to delete
   * @param executionContext the execution context
   */
  def delete[P : PEvM](state: PState[P])(implicit executionContext: ExecutionContext): Future[Deleted[P]] =
    pRepoMap(implicitly[PEv[M, P]].key).delete(state)

  /** closes any open session from the underlying database */
  def closeSession()(implicit executionContext: ExecutionContext): Future[Unit] =
    pRepoMap.values.headOption.map(_.close()).getOrElse(Future.successful(()))

}
