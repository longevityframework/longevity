package longevity.persistence

import emblem.TypeKeyMap
import longevity.exceptions.persistence.NotInDomainModelTranslationException
import longevity.model.PEv
import longevity.model.ptype.Key
import longevity.model.query.Query
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** a repository for persistent objects in a model
 * 
 * @tparam M the model
 */
abstract class Repo[M] private[persistence](private[this] val schemaCreator: SchemaCreator) {

  private[persistence] val pRepoMap: TypeKeyMap[Any, PRepo[M, ?]]

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
  def create[P: PEv[M, ?]](unpersisted: P)(implicit executionContext: ExecutionContext): Future[PState[P]] = {
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

  /** part one of a two-part call for retrieving an optional persistent object from a key value.
   *
   * the complete two-part call will end up looking like this:
   *
   * {{{
   * val f: Future[Option[PState[User]]] = repo.retrieve[User](username)
   * }}}
   *
   * the call is split into two parts this way to prevent you from having to explicitly name the
   * type of your key value. without it, the call would look like this:
   *
   * {{{
   * val f: Future[Option[PState[User]]] = repo.retrieve[User, Username](username)
   * }}}
   *
   * @see [[Retrieve.apply]]
   */
  def retrieve[P]: Retrieve[P] = new Retrieve[P]

  /** a container for part two of a two part call for retrieving an optional persistent object from a
   * key value
   *
   * @see [[Repo.retrieve]]
   */
  final class Retrieve[P] private[Repo]()  {

    /** part two of a two-part call for retrieving an optional persistent object from a key value
     *
     * @tparam V the type of the key value
     * @param keyVal the key value to use to look up the persistent object
     * @param executionContext the execution context
     *
     * @see [[Repo.retrieve]]
     */
    def apply[V : Key[M, P, ?]](
      keyVal: V)(
      implicit pEv: PEv[M, P],
      executionContext: ExecutionContext)
    : Future[Option[PState[P]]] =
      pRepoMap(implicitly[PEv[M, P]].key).retrieve[V](keyVal)
  }

  /** part one of a two-part call for retrieving a persistent object from a key value.
   *
   * the complete two-part call will end up looking like this:
   *
   * {{{
   * val f: Future[PState[User]] = repo.retrieveOne[User](username)
   * }}}
   *
   * the call is split into two parts this way to prevent you from having to explicitly name the
   * type of your key value. without it, the call would look like this:
   * 
   * {{{
   * val f: Future[PState[User]] = repo.retrieveOne[User, Username](username)
   * }}}
   * 
   * @see [[RetrieveOne.apply]]
   */
  def retrieveOne[P]: RetrieveOne[P] = new RetrieveOne[P]

  /** a container for part two of a two part call for retrieving a persistent object from a key value
   *
   * @see [[Repo.retrieveOne]]
   */
  final class RetrieveOne[P] private[Repo]()  {

    /** part two of a two-part call for retrieving a persistent object from a key value
     *
     * throws NoSuchElementException whenever the persistent ref does not refer to a persistent object
     * in the repository
     *
     * @tparam V the type of the key value
     * @param keyVal the key value to use to look up the persistent object
     * @param executionContext the execution context
     *
     * @see [[Repo.retrieveOne]]
     */
    def apply[V : Key[M, P, ?]](
      keyVal: V)(
      implicit pEv: PEv[M, P],
      executionContext: ExecutionContext)
    : Future[PState[P]] =
      pRepoMap(implicitly[PEv[M, P]].key).retrieveOne[V](keyVal)
  }

  /** retrieves multiple persistent objects matching a query
   * 
   * @param query the query to execute
   * @param executionContext the execution context
   */
  def queryToIterator[P: PEv[M, ?]](query: Query[P]): Iterator[PState[P]] =
    pRepoMap(implicitly[PEv[M, P]].key).queryToIterator(query)

  /** retrieves multiple persistent objects matching a query
   * 
   * @param query the query to execute
   * @param executionContext the execution context
   */
  def queryToFutureVec[P: PEv[M, ?]](query: Query[P])(implicit context: ExecutionContext)
  : Future[Vector[PState[P]]]
  = pRepoMap(implicitly[PEv[M, P]].key).queryToFutureVec(query)

  /** updates the persistent object
   * 
   * @param state the persistent state of the persistent object to update
   * @param executionContext the execution context
   */
  def update[P : PEv[M, ?]](state: PState[P])(implicit executionContext: ExecutionContext): Future[PState[P]] =
    pRepoMap(implicitly[PEv[M, P]].key).update(state)

  /** deletes the persistent object
   * 
   * @param state the persistent state of the persistent object to delete
   * @param executionContext the execution context
   */
  def delete[P : PEv[M, ?]](state: PState[P])(implicit executionContext: ExecutionContext): Future[Deleted[P]] =
    pRepoMap(implicitly[PEv[M, P]].key).delete(state)

  /** closes any open session from the underlying database */
  def closeSession()(implicit executionContext: ExecutionContext): Future[Unit] =
    pRepoMap.values.headOption.map(_.close()).getOrElse(Future.successful(()))

}
