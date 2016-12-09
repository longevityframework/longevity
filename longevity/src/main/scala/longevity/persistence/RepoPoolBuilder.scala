package longevity.persistence

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeBound.TypeBoundPair
import longevity.config.Cassandra
import longevity.config.InMem
import longevity.config.LongevityConfig
import longevity.config.Mongo
import longevity.config.PersistenceConfig
import longevity.config.BackEnd
import longevity.persistence.cassandra.CassandraRepo
import longevity.persistence.cassandra.CassandraRepo.CassandraSessionInfo
import longevity.persistence.inmem.InMemRepo
import longevity.persistence.mongo.MongoRepo
import longevity.persistence.mongo.MongoRepo.MongoSessionInfo
import longevity.model.Subdomain
import longevity.model.DerivedPType
import longevity.model.PType
import longevity.model.PolyPType
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

/** builds repo pools for LongevityContextImpl */
private[longevity] object RepoPoolBuilder {

  private[longevity] def buildRepoPool(
    subdomain: Subdomain,
    backEnd: BackEnd,
    config: LongevityConfig,
    test: Boolean)
  : RepoPool = {
    val pool = backEnd match {
      case InMem =>
        inMemTestRepoPool(subdomain, config)
      case Mongo =>
        val mongoConfig = if (test) config.test.mongodb else config.mongodb
        mongoRepoPool(subdomain, MongoSessionInfo(mongoConfig), config)
      case Cassandra =>
        val cassandraConfig = if (test) config.test.cassandra else config.cassandra
        cassandraRepoPool(subdomain, CassandraSessionInfo(cassandraConfig), config)
    }
    if (config.autocreateSchema) {
      Await.result(pool.createSchema()(ExecutionContext.global), Duration(1, "seconds"))
    }
    pool
  }

  private trait StockRepoFactory[R[P] <: BaseRepo[P]] {
    def build[P](
      pType: PType[P],
      polyRepoOpt: Option[R[_ >: P]] = None)
    : R[P]
  }

  private def inMemTestRepoPool(subdomain: Subdomain, persistenceConfig: PersistenceConfig): RepoPool = {
    object repoFactory extends StockRepoFactory[InMemRepo] {
      def build[P](
        pType: PType[P],
        polyRepoOpt: Option[InMemRepo[_ >: P]])
      : InMemRepo[P] =
        InMemRepo[P](pType, subdomain, persistenceConfig, polyRepoOpt)
    }
    buildRepoPool(subdomain, repoFactory, SchemaCreator.empty, persistenceConfig)
  }

  private def mongoRepoPool(
    subdomain: Subdomain,
    session: MongoSessionInfo,
    persistenceConfig: PersistenceConfig)
  : RepoPool = {
    object repoFactory extends StockRepoFactory[MongoRepo] {
      def build[P](
        pType: PType[P],
        polyRepoOpt: Option[MongoRepo[_ >: P]])
      : MongoRepo[P] =
        MongoRepo[P](pType, subdomain, session, persistenceConfig, polyRepoOpt)
    }
    buildRepoPool(subdomain, repoFactory, SchemaCreator.empty, persistenceConfig)
  }

  private def cassandraRepoPool(
    subdomain: Subdomain,
    session: CassandraSessionInfo,
    persistenceConfig: PersistenceConfig)
  : RepoPool = {
    object repoFactory extends StockRepoFactory[CassandraRepo] {
      def build[P](
        pType: PType[P],
        polyRepoOpt: Option[CassandraRepo[_ >: P]])
      : CassandraRepo[P] =
        CassandraRepo[P](pType, subdomain, session, persistenceConfig, polyRepoOpt)
    }
    buildRepoPool(subdomain, repoFactory, session, persistenceConfig)
  }

  private def buildRepoPool[R[P] <: BaseRepo[P]](
    subdomain: Subdomain,
    stockRepoFactory: StockRepoFactory[R],
    schemaCreator: SchemaCreator,
    persistenceConfig: PersistenceConfig)
  : RepoPool = {
    var keyToRepoMap = TypeKeyMap[Any, R]
    type Pair[P] = TypeBoundPair[Any, TypeKey, PType, P]
    def createRepoFromPair[P](pair: Pair[P]): Unit = {
      val pTypeKey = pair._1
      val pType = pair._2

      val polyKey: Option[TypeKey[_ >: P]] = pType match {
        case dpt: DerivedPType[_, _] => Some(dpt.polyPTypeKey)
        case _ => None
      }

      val repo = stockRepoFactory.build[P](pType, polyKey.map(keyToRepoMap(_)))
      keyToRepoMap += (pTypeKey -> repo)

    }
    subdomain.pTypePool.filter(isPolyPType).iterator.foreach { pair => createRepoFromPair(pair) }
    subdomain.pTypePool.filterNot(isPolyPType).iterator.foreach { pair => createRepoFromPair(pair) }
    val repoPool = new RepoPool(keyToRepoMap.widen[BaseRepo], schemaCreator)
    finishRepoInitialization(repoPool)
    autocreateSchema(repoPool, persistenceConfig)
    repoPool
  }

  private def isPolyPType(pair: TypeBoundPair[Any, TypeKey, PType, _]): Boolean = {
    pair._2.isInstanceOf[PolyPType[_]]
  }

  private def finishRepoInitialization(repoPool: RepoPool): Unit = {
    repoPool.baseRepoMap.values.foreach { repo => repo._repoPoolOption = Some(repoPool) }
  }

  private def autocreateSchema(repoPool: RepoPool, persistenceConfig: PersistenceConfig): Unit = {
    if (persistenceConfig.autocreateSchema) {
      import scala.concurrent.Await
      import scala.concurrent.duration.Duration
      import scala.concurrent.ExecutionContext.Implicits.global
      Await.result(repoPool.createSchema(), Duration(10, "seconds"))
    }
  }

}
