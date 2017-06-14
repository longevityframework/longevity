package longevity.persistence

import longevity.config.BackEnd
import longevity.config.Cassandra
import longevity.config.InMem
import longevity.config.JDBC
import longevity.config.LongevityConfig
import longevity.config.MongoDB
import longevity.config.PersistenceConfig
import longevity.config.SQLite
import longevity.exceptions.persistence.NotInDomainModelTranslationException
import longevity.model.DerivedPType
import longevity.model.ModelType
import longevity.model.PEv
import longevity.model.PType
import longevity.model.PolyPType
import longevity.model.ptype.Key
import longevity.model.query.Query
import longevity.persistence.cassandra.CassandraRepo
import longevity.persistence.inmem.InMemRepo
import longevity.persistence.jdbc.JdbcRepo
import longevity.persistence.mongo.MongoRepo
import longevity.persistence.sqlite.SQLiteRepo
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import typekey.TypeKeyMap

/** a repository for persistent objects in a model
 * 
 * @tparam M the model
 */
abstract class Repo[M] private[persistence](
  protected val modelType: ModelType[M],
  protected val persistenceConfig: PersistenceConfig) {

  type R[P] <: PRepo[M, P]

  private[persistence] val pRepoMap: TypeKeyMap[Any, PRepo[M, ?]] = {
    var keyToRepoMap = TypeKeyMap[Any, R]
    def createRepo[P](pType: PType[M, P]): Unit = {
      val polyRepo: Option[R[_ >: P]] = pType match {
        case dpt: DerivedPType[_, _, _] => Some(keyToRepoMap(dpt.polyPTypeKey))
        case _ => None
      }
      keyToRepoMap += (pType.pTypeKey -> buildPRepo[P](pType, polyRepo))
    }

    // build the poly repos first
    val (polys, nonPolys) = modelType.pTypePool.values.partition(_.isInstanceOf[PolyPType[_, _]])
    polys.foreach { pType => createRepo(pType) }
    nonPolys.foreach { pType => createRepo(pType) }

    // finish repo initialization
    // TODO: cant I pass in `this` to PRepo ctors?
    keyToRepoMap.values.foreach { _._repoOption = Some(this) }

    keyToRepoMap.widen[PRepo[M, ?]]    
  }

  protected def buildPRepo[P](pType: PType[M, P], polyRepoOpt: Option[R[_ >: P]] = None): R[P]

  /** opens a connection to the underlying database */
  def openConnection()(implicit context: ExecutionContext): Future[Unit] = Future(openConnectionBlocking())

  private def openConnectionBlocking(): Unit = {
    openBaseConnectionBlocking()
    if (persistenceConfig.autoCreateSchema) {
      createSchemaBlocking()
    }
  }

  protected def openBaseConnectionBlocking(): Unit

  /** non-desctructively creates any needed database constructs */
  def createSchema()(implicit context: ExecutionContext): Future[Unit] = Future(createSchemaBlocking())

  private def createSchemaBlocking(): Unit = {
    createBaseSchemaBlocking()
    val (polys, nonPolys) = pRepoMap.values.partition(_.isInstanceOf[BasePolyRepo[M, _]])
    polys.foreach(_.createSchemaBlocking())
    nonPolys.foreach(_.createSchemaBlocking())
  }

  protected def createBaseSchemaBlocking(): Unit

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

  /** creates many persistent objects at once
   *
   * because [[PWithEv]] is an implicit class, you can call this method using just persistent
   * objects, and they will be converted to `PWithEv` implicitly:
   *
   * {{{
   * repo.createMany(user1, user2, user2, blogPost1, blogPost2, blog)
   * }}}
   *
   * @param pWithEvs the persistent objects to persist, wrapped with their evidence
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

  /** closes an open connection from the underlying database */
  def closeConnection()(implicit executionContext: ExecutionContext): Future[Unit] =
    Future(closeConnectionBlocking())

  protected def closeConnectionBlocking(): Unit

}

private[longevity] object Repo {

  def apply[M](
    modelType: ModelType[M],
    backEnd: BackEnd,
    config: LongevityConfig,
    test: Boolean)
  : Repo[M] = {
    val repo = backEnd match {
      case Cassandra =>
        val cassandraConfig = if (test) config.test.cassandra else config.cassandra
        new CassandraRepo(modelType, config, cassandraConfig)
      case InMem =>
        new InMemRepo(modelType, config)
      case MongoDB =>
        val mongoConfig = if (test) config.test.mongodb else config.mongodb
        new MongoRepo(modelType, config, mongoConfig)
      case SQLite =>
        val jdbcConfig = if (test) config.test.jdbc else config.jdbc
        new SQLiteRepo(modelType, config, jdbcConfig)
      case JDBC =>
        val jdbcConfig = if (test) config.test.jdbc else config.jdbc
        new JdbcRepo(modelType, config, jdbcConfig)
    }
    if (config.autoOpenConnection) {
      repo.openConnectionBlocking()
    }
    repo
  }

}
