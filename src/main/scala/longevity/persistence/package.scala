package longevity

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session
import com.mongodb.casbah.Imports._
import com.typesafe.config.Config
import emblem.TypeBoundMap
import emblem.TypeBoundPair
import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeKey
import longevity.context.Cassandra
import longevity.context.InMem
import longevity.context.Mongo
import longevity.context.PersistenceStrategy
import longevity.persistence.cassandra.CassandraRepo
import longevity.persistence.mongo.MongoRepo
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.PType
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** manages entity persistence operations */
package object persistence {

  /** packages a [[longevity.subdomain.persistent.Persistent persistent entity]]
   * with a `TypeKey` for the entity's type. used by [[RepoPool.createMany]].
   */
  implicit class PWithTypeKey[P <: Persistent : TypeKey](val p: P) {
    val pTypeKey = typeKey[P]
  }

  /** a future persistent state */
  type FPState[P <: Persistent] = Future[PState[P]]

  /** extension methods for an [[FPState]] */
  implicit class LiftFPState[P <: Persistent](fpState: FPState[P]) {

    /** map the future PState by mapping the entity inside the PState */
    def mapRoot(f: P => P): FPState[P] =
      fpState.map { pState => pState.map { p => f(p) } }

    /** flatMap the future PState by mapping the entity inside the PState into a `Future[P]` */
    def flatMapRoot(f: P => Future[P]): FPState[P] =
      fpState.flatMap { pState => f(pState.get) map { p => pState.set(p) } }

  }

  /** a future option persistent state */
  type FOPState[P <: Persistent] = Future[Option[PState[P]]]

  /** extension methods for an [[FOPState]] */
  implicit class LiftFOPState[P <: Persistent](fopState: FOPState[P]) {

    /** map the `FOPState` by mapping the entity inside the PState */
    def mapRoot(f: P => P): FOPState[P] =
      fopState.map { opState =>
        opState.map { pState => pState.map { p => f(p) } }
      }

    /** flatMap the `FOPState` by mapping the entity inside the PState into a `Future[P]` */
    def flatMapRoot(f: P => Future[P]): FOPState[P] =
      fopState.flatMap { opState =>
        opState match {
          case Some(pState) => f(pState.get) map { p => Some(pState.set(p)) }
          case None => Future.successful(None)
        }
      }

    def mapState(f: PState[P] => PState[P]): FOPState[P] =
      fopState.map { opState => opState.map(f(_)) }

    def flatMapState(f: PState[P] => FPState[P]): FOPState[P] =
      fopState.flatMap { opState =>
        opState match {
          case Some(pState) => f(pState).map(Some(_))
          case None => Future.successful(None)
        }
      }

  }

  // TODO NEXT:
  // - repo behavior has to vary based on BaseType/DerivedType:
  //   - DerivedType needs to use the table of the base type
  //   - DerivedType schema gen mods
  //   - DerivedType inserts/updates have to put in the descriminator
  //   - DerivedType retrieveByAssoc has to include descriminator in query
  //   - DerivedType retrieveByQuery has to include descriminator in query
  //
  //   - InMem
  //   - Mongo
  //   - Cassandra
  // - specialized integration tests to test that the BaseType repo and DerivedType repos share a table

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
      def build[P <: Persistent](pType: PType[P], pTypeKey: TypeKey[P]): BaseRepo[P] =
        new InMemRepo(pType, subdomain)(pTypeKey)
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
      def build[P <: Persistent](pType: PType[P], pTypeKey: TypeKey[P]): BaseRepo[P] =
        new MongoRepo(pType, subdomain, mongoDB)(pTypeKey)
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

  private def cassandraRepoPool(subdomain: Subdomain, session: Session): RepoPool = {
    object repoFactory extends StockRepoFactory {
      def build[P <: Persistent](pType: PType[P], pTypeKey: TypeKey[P]): BaseRepo[P] =
        new CassandraRepo(pType, subdomain, session)(pTypeKey)
    }
    buildRepoPool(subdomain, repoFactory)
  }

  private trait StockRepoFactory {
    def build[P <: Persistent](pType: PType[P], pTypeKey: TypeKey[P]): BaseRepo[P]
  }

  private def buildRepoPool(
    subdomain: Subdomain,
    stockRepoFactory: StockRepoFactory)
  : RepoPool = {
    var typeKeyMap = emptyTypeKeyMap
    type Pair[P <: Persistent] = TypeBoundPair[Persistent, TypeKey, PType, P]
    def createRepoFromPair[P <: Persistent](pair: Pair[P]): Unit = {
      val pTypeKey = pair._1
      val pType = pair._2
      val repo = stockRepoFactory.build(pType, pTypeKey)
      typeKeyMap += (pTypeKey -> repo)
    }
    subdomain.pTypePool.iterator.foreach { pair => createRepoFromPair(pair) }
    val repoPool = new RepoPool(typeKeyMap)
    finishRepoInitialization(repoPool)
    repoPool
  }

  private val emptyTypeKeyMap = TypeKeyMap[Persistent, BaseRepo]

  private def finishRepoInitialization(repoPool: RepoPool): Unit = {
    repoPool.baseRepoMap.values.foreach { repo => repo._repoPoolOption = Some(repoPool) }
  }

  // stuff for {Repo,RepoPool}.createMany. also used by RepoCrudSpec.randomP

  private[longevity] type PIdentity[P <: Persistent] = P

  private[longevity] type CreatedCache = TypeBoundMap[Persistent, PIdentity, PState]

  private[longevity] object CreatedCache {
    def apply() = TypeBoundMap[Persistent, PIdentity, PState]()
  }

}
