package longevity

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session
import com.mongodb.casbah.Imports._
import com.typesafe.config.Config
import emblem.TypeBoundPair
import emblem.imports._
import longevity.context._
import longevity.persistence.cassandra.CassandraRepo
import longevity.persistence.mongo.MongoRepo
import longevity.subdomain._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** manages entity persistence operations */
package object persistence {

  /** packages a [Root] with a `TypeKey` for the root's type. used
   * by [[RepoPool.createMany]].
   */
  implicit class RootWithTypeKey[R <: Root : TypeKey](val root: R) {
    val rootTypeKey = typeKey[R]
  }

  /** a future persistent state */
  type FPState[R <: Root] = Future[PState[R]]

  /** extension methods for an [[FPState]] */
  implicit class LiftFPState[R <: Root](fpState: FPState[R]) {

    /** map the future PState by mapping the root inside the PState */
    def mapRoot(f: R => R): FPState[R] =
      fpState.map { pState => pState.map { root => f(root) } }

    /** flatMap the future PState by mapping the root inside the PState into a `Future[Root]` */
    def flatMapRoot(f: R => Future[R]): FPState[R] =
      fpState.flatMap { pState => f(pState.get) map { root => pState.set(root) } }

  }

  /** a future option persistent state */
  type FOPState[R <: Root] = Future[Option[PState[R]]]

  /** extension methods for an [[FOPState]] */
  implicit class LiftFOPState[R <: Root](fopState: FOPState[R]) {

    /** map the `FOPState` by mapping the root inside the PState */
    def mapRoot(f: R => R): FOPState[R] =
      fopState.map { opState =>
        opState.map { pState => pState.map { root => f(root) } }
      }

    /** flatMap the `FOPState` by mapping the root inside the PState into a `Future[Root]` */
    def flatMapRoot(f: R => Future[R]): FOPState[R] =
      fopState.flatMap { opState =>
        opState match {
          case Some(pState) => f(pState.get) map { root => Some(pState.set(root)) }
          case None => Future.successful(None)
        }
      }

    def mapState(f: PState[R] => PState[R]): FOPState[R] =
      fopState.map { opState => opState.map(f(_)) }

    def flatMapState(f: PState[R] => FPState[R]): FOPState[R] =
      fopState.flatMap { opState =>
        opState match {
          case Some(pState) => f(pState).map(Some(_))
          case None => Future.successful(None)
        }
      }

  }

  private[longevity] def buildRepoPool(
    subdomain: Subdomain,
    persistenceStrategy: PersistenceStrategy,
    config: Config)
  : RepoPool =
    persistenceStrategy match {
      case InMem => inMemRepoPool(subdomain)
      case Mongo => mongoRepoPool(subdomain, mongoDb(config))
      case Cassandra => cassandraRepoPool(subdomain, cassandraSession(config))
    }

  private def inMemRepoPool(subdomain: Subdomain): RepoPool = {
    object repoFactory extends StockRepoFactory {
      def build[R <: Root](entityType: RootType[R], entityKey: TypeKey[R]): BaseRepo[R] =
        new InMemRepo(entityType, subdomain)(entityKey)
    }
    buildRepoPool(subdomain, repoFactory)
  }

  private def mongoDb(config: Config): MongoDB = {
    val mongoClient = MongoClient(config.getString("mongodb.uri"))
    val mongoDb = mongoClient.getDB(config.getString("mongodb.db"))

    import com.mongodb.casbah.commons.conversions.scala._
    RegisterJodaTimeConversionHelpers()

    mongoDb
  }

  private def mongoRepoPool(subdomain: Subdomain, mongoDB: MongoDB): RepoPool = {
    object repoFactory extends StockRepoFactory {
      def build[R <: Root](entityType: RootType[R], entityKey: TypeKey[R]): BaseRepo[R] =
        new MongoRepo(entityType, subdomain, mongoDB)(entityKey)
    }
    buildRepoPool(subdomain, repoFactory)
  }

  private def cassandraSession(config: Config): Session = {
    val builder = Cluster.builder.addContactPoint(config.getString("cassandra.address"))
    if (config.getBoolean("cassandra.useCredentials")) {
      builder.withCredentials(
        config.getString("cassandra.username"),
        config.getString("cassandra.password"))
    }
    val cluster = builder.build
    val session = cluster.connect();
    val keyspace = config.getString("cassandra.keyspace")
    val replicationFactor = config.getInt("cassandra.replicationFactor")
    session.execute(s"""|CREATE KEYSPACE IF NOT EXISTS $keyspace
                    |WITH replication = {
                    |  'class': 'SimpleStrategy',
                    |  'replication_factor': $replicationFactor
                    |};
                    |""".stripMargin)
    session.execute(s"use $keyspace")
    session
  }
  
  private def cassandraRepoPool(subdomain: Subdomain, session: Session)
  : RepoPool = {
    object repoFactory extends StockRepoFactory {
      def build[R <: Root](rootType: RootType[R], rootTypeKey: TypeKey[R]): BaseRepo[R] =
        new CassandraRepo(rootType, subdomain, session)(rootTypeKey)
    }
    buildRepoPool(subdomain, repoFactory)
  }

  private trait StockRepoFactory {
    def build[R <: Root](entityType: RootType[R], entityKey: TypeKey[R]): BaseRepo[R]
  }

  private def buildRepoPool(
    subdomain: Subdomain,
    stockRepoFactory: StockRepoFactory)
  : RepoPool = {
    var typeKeyMap = emptyTypeKeyMap
    type Pair[RE <: Root] = TypeBoundPair[Root, TypeKey, RootType, RE]
    def createRepoFromPair[RE <: Root](pair: Pair[RE]): Unit = {
      val entityKey = pair._1
      val entityType = pair._2
      val repo = stockRepoFactory.build(entityType, entityKey)
      typeKeyMap += (entityKey -> repo)
    }
    subdomain.rootTypePool.iterator.foreach { pair => createRepoFromPair(pair) }
    val repoPool = new RepoPool(typeKeyMap)
    finishRepoInitialization(repoPool)
    repoPool
  }

  private val emptyTypeKeyMap = TypeKeyMap[Root, BaseRepo]

  private def finishRepoInitialization(repoPool: RepoPool): Unit = {
    repoPool.baseRepoMap.values.foreach { repo => repo._repoPoolOption = Some(repoPool) }
  }

  // stuff for {Repo,RepoPool}.createMany. also used by RepoCrudSpec.randomRoot

  private[longevity] type RootIdentity[R <: Root] = Root

  private[longevity] type CreatedCache = TypeBoundMap[Root, RootIdentity, PState]

  private[longevity] object CreatedCache {
    def apply() = TypeBoundMap[Root, RootIdentity, PState]()
  }

}
