package longevity.persistence

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeBound.TypeBoundPair
import longevity.config.BackEnd
import longevity.config.Cassandra
import longevity.config.InMem
import longevity.config.LongevityConfig
import longevity.config.MongoDB
import longevity.config.PersistenceConfig
import longevity.config.SQLite
import longevity.model.DerivedPType
import longevity.model.DomainModel
import longevity.model.PType
import longevity.model.PolyPType
import longevity.persistence.cassandra.CassandraRepo
import longevity.persistence.cassandra.CassandraRepo.CassandraSessionInfo
import longevity.persistence.inmem.InMemRepo
import longevity.persistence.mongo.MongoRepo
import longevity.persistence.mongo.MongoRepo.MongoSessionInfo
import longevity.persistence.sqlite.SQLiteRepo
import longevity.persistence.sqlite.SQLiteRepo.SQLiteSessionInfo
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

/** builds repo pools for LongevityContextImpl */
private[longevity] object RepoPoolBuilder {

  private[longevity] def buildRepoPool(
    domainModel: DomainModel,
    backEnd: BackEnd,
    config: LongevityConfig,
    test: Boolean)
  : RepoPool = {
    val pool = backEnd match {
      case Cassandra =>
        val cassandraConfig = if (test) config.test.cassandra else config.cassandra
        cassandraRepoPool(domainModel, CassandraSessionInfo(cassandraConfig), config)
      case InMem =>
        inMemTestRepoPool(domainModel, config)
      case MongoDB =>
        val mongoConfig = if (test) config.test.mongodb else config.mongodb
        mongoRepoPool(domainModel, MongoSessionInfo(mongoConfig), config)
      case SQLite =>
        val sqliteConfig = if (test) config.test.jdbc else config.jdbc
        sqliteRepoPool(domainModel, SQLiteSessionInfo(sqliteConfig), config)
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

  private def cassandraRepoPool(
    domainModel: DomainModel,
    session: CassandraSessionInfo,
    persistenceConfig: PersistenceConfig)
  : RepoPool = {
    object repoFactory extends StockRepoFactory[CassandraRepo] {
      def build[P](
        pType: PType[P],
        polyRepoOpt: Option[CassandraRepo[_ >: P]])
      : CassandraRepo[P] =
        CassandraRepo[P](pType, domainModel, session, persistenceConfig, polyRepoOpt)
    }
    buildRepoPool(domainModel, repoFactory, session, persistenceConfig)
  }

  private def inMemTestRepoPool(domainModel: DomainModel, persistenceConfig: PersistenceConfig): RepoPool = {
    object repoFactory extends StockRepoFactory[InMemRepo] {
      def build[P](
        pType: PType[P],
        polyRepoOpt: Option[InMemRepo[_ >: P]])
      : InMemRepo[P] =
        InMemRepo[P](pType, domainModel, persistenceConfig, polyRepoOpt)
    }
    buildRepoPool(domainModel, repoFactory, SchemaCreator.empty, persistenceConfig)
  }

  private def mongoRepoPool(
    domainModel: DomainModel,
    session: MongoSessionInfo,
    persistenceConfig: PersistenceConfig)
  : RepoPool = {
    object repoFactory extends StockRepoFactory[MongoRepo] {
      def build[P](
        pType: PType[P],
        polyRepoOpt: Option[MongoRepo[_ >: P]])
      : MongoRepo[P] =
        MongoRepo[P](pType, domainModel, session, persistenceConfig, polyRepoOpt)
    }
    buildRepoPool(domainModel, repoFactory, SchemaCreator.empty, persistenceConfig)
  }

  private def sqliteRepoPool(
    domainModel: DomainModel,
    session: SQLiteSessionInfo,
    persistenceConfig: PersistenceConfig)
  : RepoPool = {
    object repoFactory extends StockRepoFactory[SQLiteRepo] {
      def build[P](
        pType: PType[P],
        polyRepoOpt: Option[SQLiteRepo[_ >: P]])
      : SQLiteRepo[P] =
        SQLiteRepo[P](pType, domainModel, session, persistenceConfig, polyRepoOpt)
    }
    buildRepoPool(domainModel, repoFactory, SchemaCreator.empty, persistenceConfig)
  }

  private def buildRepoPool[R[P] <: BaseRepo[P]](
    domainModel: DomainModel,
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
    domainModel.pTypePool.filter(isPolyPType).iterator.foreach { pair => createRepoFromPair(pair) }
    domainModel.pTypePool.filterNot(isPolyPType).iterator.foreach { pair => createRepoFromPair(pair) }
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
