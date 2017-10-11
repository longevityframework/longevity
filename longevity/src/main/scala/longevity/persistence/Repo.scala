package longevity.persistence

import journal.Logger
import longevity.config.BackEnd
import longevity.config.Cassandra
import longevity.config.InMem
import longevity.config.JDBC
import longevity.config.LongevityConfig
import longevity.config.MongoDB
import longevity.config.PersistenceConfig
import longevity.config.SQLite
import longevity.effect.Effect
import longevity.effect.Effect.Syntax
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
import typekey.TypeKeyMap

/** a repository for persistent objects in a model
 * 
 * @tparam F the effect
 * @tparam M the model
 */
abstract class Repo[F[_], M] private[persistence](
  protected val effect: Effect[F],
  protected val modelType: ModelType[M],
  protected val persistenceConfig: PersistenceConfig) {

  protected[persistence] implicit def implicitF = effect
  protected val logger = Logger[this.type]

  private[persistence] type R[P] <: PRepo[F, M, P]

  private[persistence] val pRepoMap: TypeKeyMap[Any, R] = {
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
    keyToRepoMap.values.foreach { _._repoOption = Some(this) }

    keyToRepoMap
  }

  protected def buildPRepo[P](pType: PType[M, P], polyRepoOpt: Option[R[_ >: P]] = None): R[P]

  /** opens a connection to the underlying database */
  def openConnection: F[Unit] = effect.pure(()).mapBlocking(_ => openConnectionBlocking())

  private def openConnectionBlocking(): Unit = {
    openBaseConnectionBlocking()
    if (persistenceConfig.autoCreateSchema) {
      createSchemaBlocking()
    }
  }

  protected def openBaseConnectionBlocking(): Unit

  /** non-desctructively creates any needed database constructs */
  def createSchema: F[Unit] = effect.pure(()).mapBlocking(_ => createSchemaBlocking())

  private def createSchemaBlocking(): Unit = {
    createBaseSchemaBlocking()
    val (polys, nonPolys) = pRepoMap.values.partition(_.isInstanceOf[BasePolyRepo[F, M, _]])
    polys.foreach(_.createSchemaBlocking())
    nonPolys.foreach(_.createSchemaBlocking())
  }

  protected def createBaseSchemaBlocking(): Unit

  private[longevity] def createMigrationSchema(): F[Unit] =
    effect.pure(()).mapBlocking(_ => createMigrationSchemaBlocking())

  private def createMigrationSchemaBlocking(): Unit = {
    logger.debug(s"creating migration schema")
    pRepoMap.values.foreach(_.createMigrationSchemaBlocking())
    logger.debug(s"done creating migration schema")
  }

  private[longevity] def dropSchema(): F[Unit] =
    effect.pure(()).mapBlocking(_ => dropSchemaBlocking())

  private def dropSchemaBlocking(): Unit = {
    logger.debug(s"dropping schema")
    pRepoMap.values.foreach(_.dropSchemaBlocking())
    logger.debug(s"done dropping schema")
  }

  /** creates the persistent object
   *
   * @param unpersisted the persistent object to create
   */
  def create[P : PEv[M, ?]](unpersisted: P): F[PState[P]] = for {
    pr <- pRepoF[P]
    ps <- pr.create(unpersisted)
  } yield ps

  /** creates the persistent object with persistent state inherited from another incarnation
   *
   * @param state the persistent state to create
   */
  private[longevity] def createState[P : PEv[M, ?]](state: PState[P]): F[PState[P]] = for {
    pr <- pRepoF[P]
    ps <- pr.createState(state)
  } yield ps

  /** part one of a two-part call for retrieving an optional persistent object from a key value.
   *
   * the complete two-part call will end up looking like this:
   *
   * {{{
   * val f: F[Option[PState[User]]] = repo.retrieve[User](username)
   * }}}
   *
   * the call is split into two parts this way to prevent you from having to explicitly name the
   * type of your key value. without it, the call would look like this:
   *
   * {{{
   * val f: F[Option[PState[User]]] = repo.retrieve[User, Username](username)
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
     *
     * @see [[Repo.retrieve]]
     */
    def apply[V](keyVal: V)(implicit pEv: PEv[M, P], key: Key[M, P, V]): F[Option[PState[P]]] = for {
      pr <- pRepoF[P]
      pso <- pr.retrieve[V](keyVal)
    } yield pso

  }

  /** part one of a two-part call for retrieving a persistent object from a key value.
   *
   * the complete two-part call will end up looking like this:
   *
   * {{{
   * val f: F[PState[User]] = repo.retrieveOne[User](username)
   * }}}
   *
   * the call is split into two parts this way to prevent you from having to explicitly name the
   * type of your key value. without it, the call would look like this:
   * 
   * {{{
   * val f: F[PState[User]] = repo.retrieveOne[User, Username](username)
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
     *
     * @see [[Repo.retrieveOne]]
     */
    def apply[V](keyVal: V)(implicit pEv: PEv[M, P], key: Key[M, P, V]): F[PState[P]] = for {
      pr <- pRepoF[P]
      ps <- pr.retrieveOne[V](keyVal)
    } yield ps

  }

  /** retrieves multiple persistent objects matching a query
   * 
   * @param query the query to execute
   */
  def queryToIterator[P: PEv[M, ?]](query: Query[P]): F[Iterator[PState[P]]] = for {
    pr <- pRepoF[P]
    i  <- pr.queryToIterator(query)
  } yield i

  /** retrieves multiple persistent objects matching a query
   * 
   * @param query the query to execute
   */
  def queryToVector[P: PEv[M, ?]](query: Query[P]): F[Vector[PState[P]]] = for {
    pr <- pRepoF[P]
    v  <- pr.queryToVector(query)
  } yield v

  /** updates the persistent object
   * 
   * @param state the persistent state of the persistent object to update
   */
  def update[P : PEv[M, ?]](state: PState[P]): F[PState[P]] = for {
    pr <- pRepoF[P]
    ps <- pr.update(state)
  } yield ps

  private[longevity] def updateMigrationStarted[P : PEv[M, ?]](state: PState[P]): F[Unit] = for {
    pr <- pRepoF[P]
    _  <- pr.updateMigrationStarted(state)
  } yield ()

  private[longevity] def updateMigrationComplete[P : PEv[M, ?]](state: PState[P]): F[Unit] = for {
    pr <- pRepoF[P]
    _  <- pr.updateMigrationComplete(state)
  } yield ()

  /** deletes the persistent object
   * 
   * @param state the persistent state of the persistent object to delete
   */
  def delete[P : PEv[M, ?]](state: PState[P]): F[Deleted[P]] = for {
    pr <- pRepoF[P]
    d  <- pr.delete(state)
  } yield d

  /** closes an open connection from the underlying database */
  def closeConnection: F[Unit] = effect.pure(()).mapBlocking(_ => closeConnectionBlocking())

  protected def closeConnectionBlocking(): Unit

  private[persistence] def pRepoF[P : PEv[M, ?]]: F[R[P]] = {
    val key = implicitly[PEv[M, P]].key
    pRepoMap.get(key) match {
      case Some(pr) => effect.pure(pr)
      case None =>
        // this should never happen under normal usage conditions. but there are some ways the user
        // can subvert the design to get their hands on a PEv[M, P] for a P that is not in the model
        throw new NotInDomainModelTranslationException(key.name)
    }
  }

}

private[longevity] object Repo {

  def apply[F[_], M](
    effect: Effect[F],
    modelType: ModelType[M],
    backEnd: BackEnd,
    config: LongevityConfig,
    test: Boolean)
  : Repo[F, M] = {
    val repo = backEnd match {
      case Cassandra =>
        val cassandraConfig = if (test) config.test.cassandra else config.cassandra
        new CassandraRepo(effect, modelType, config, cassandraConfig)
      case InMem =>
        new InMemRepo(effect, modelType, config)
      case MongoDB =>
        val mongoConfig = if (test) config.test.mongodb else config.mongodb
        new MongoRepo(effect, modelType, config, mongoConfig)
      case SQLite =>
        val jdbcConfig = if (test) config.test.jdbc else config.jdbc
        new SQLiteRepo(effect, modelType, config, jdbcConfig)
      case JDBC =>
        val jdbcConfig = if (test) config.test.jdbc else config.jdbc
        new JdbcRepo(effect, modelType, config, jdbcConfig)
    }
    if (config.autoOpenConnection) {
      repo.openConnectionBlocking()
    }
    repo
  }

}
