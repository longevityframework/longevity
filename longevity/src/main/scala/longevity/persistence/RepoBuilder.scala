package longevity.persistence

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeBound.TypeBoundPair
import longevity.config.BackEnd
import longevity.config.Cassandra
import longevity.config.InMem
import longevity.config.JDBC
import longevity.config.LongevityConfig
import longevity.config.MongoDB
import longevity.config.PersistenceConfig
import longevity.config.SQLite
import longevity.model.DerivedPType
import longevity.model.ModelType
import longevity.model.PType
import longevity.model.PolyPType
import longevity.persistence.cassandra.CassandraRepo
import longevity.persistence.cassandra.CassandraRepo.CassandraSessionInfo
import longevity.persistence.inmem.InMemRepo
import longevity.persistence.jdbc.JdbcRepo
import longevity.persistence.mongo.MongoRepo
import longevity.persistence.mongo.MongoRepo.MongoSessionInfo
import longevity.persistence.sqlite.SQLiteRepo
import longevity.persistence.jdbc.JdbcRepo.JdbcSessionInfo
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

/** builds repos for LongevityContextImpl */
private[longevity] object RepoBuilder {

  // TODO please factor away some of this duplicitous code

  private[longevity] def buildRepo[M](
    modelType: ModelType[M],
    backEnd: BackEnd,
    config: LongevityConfig,
    test: Boolean)
  : Repo[M] = {
    val repo = backEnd match {
      case Cassandra =>
        val cassandraConfig = if (test) config.test.cassandra else config.cassandra
        cassandraRepo(modelType, CassandraSessionInfo(cassandraConfig), config)
      case InMem =>
        inMemTestRepo(modelType, config)
      case MongoDB =>
        val mongoConfig = if (test) config.test.mongodb else config.mongodb
        mongoRepo(modelType, MongoSessionInfo(mongoConfig), config)
      case SQLite =>
        val jdbcConfig = if (test) config.test.jdbc else config.jdbc
        sqliteRepo(modelType, JdbcSessionInfo(jdbcConfig), config)
      case JDBC =>
        val jdbcConfig = if (test) config.test.jdbc else config.jdbc
        jdbcRepo(modelType, JdbcSessionInfo(jdbcConfig), config)
    }
    if (config.autocreateSchema) {
      Await.result(repo.createSchema()(ExecutionContext.global), Duration(1, "seconds"))
    }
    repo
  }

  private trait StockRepoFactory[R[P] <: PRepo[P]] {
    def build[P](
      pType: PType[P],
      polyRepoOpt: Option[R[_ >: P]] = None)
    : R[P]
  }

  private def cassandraRepo[M](
    modelType: ModelType[M],
    session: CassandraSessionInfo,
    persistenceConfig: PersistenceConfig)
  : Repo[M] = {
    object repoFactory extends StockRepoFactory[CassandraRepo] {
      def build[P](
        pType: PType[P],
        polyRepoOpt: Option[CassandraRepo[_ >: P]])
      : CassandraRepo[P] =
        CassandraRepo[P](pType, modelType, session, persistenceConfig, polyRepoOpt)
    }
    buildRepo(modelType, repoFactory, session, persistenceConfig)
  }

  private def inMemTestRepo[M](modelType: ModelType[M], persistenceConfig: PersistenceConfig): Repo[M] = {
    object repoFactory extends StockRepoFactory[InMemRepo] {
      def build[P](
        pType: PType[P],
        polyRepoOpt: Option[InMemRepo[_ >: P]])
      : InMemRepo[P] =
        InMemRepo[P](pType, modelType, persistenceConfig, polyRepoOpt)
    }
    buildRepo(modelType, repoFactory, SchemaCreator.empty, persistenceConfig)
  }

  private def mongoRepo[M](
    modelType: ModelType[M],
    session: MongoSessionInfo,
    persistenceConfig: PersistenceConfig)
  : Repo[M] = {
    object repoFactory extends StockRepoFactory[MongoRepo] {
      def build[P](
        pType: PType[P],
        polyRepoOpt: Option[MongoRepo[_ >: P]])
      : MongoRepo[P] =
        MongoRepo[P](pType, modelType, session, persistenceConfig, polyRepoOpt)
    }
    buildRepo(modelType, repoFactory, SchemaCreator.empty, persistenceConfig)
  }

  private def sqliteRepo[M](
    modelType: ModelType[M],
    session: JdbcSessionInfo,
    persistenceConfig: PersistenceConfig)
  : Repo[M] = {
    object repoFactory extends StockRepoFactory[SQLiteRepo] {
      def build[P](
        pType: PType[P],
        polyRepoOpt: Option[SQLiteRepo[_ >: P]])
      : SQLiteRepo[P] =
        SQLiteRepo[P](pType, modelType, session, persistenceConfig, polyRepoOpt)
    }
    buildRepo(modelType, repoFactory, SchemaCreator.empty, persistenceConfig)
  }

  private def jdbcRepo[M](
    modelType: ModelType[M],
    session: JdbcSessionInfo,
    persistenceConfig: PersistenceConfig)
  : Repo[M] = {
    object repoFactory extends StockRepoFactory[JdbcRepo] {
      def build[P](
        pType: PType[P],
        polyRepoOpt: Option[JdbcRepo[_ >: P]])
      : JdbcRepo[P] =
        JdbcRepo[P](pType, modelType, session, persistenceConfig, polyRepoOpt)
    }
    buildRepo(modelType, repoFactory, SchemaCreator.empty, persistenceConfig)
  }

  private def buildRepo[M, R[P] <: PRepo[P]](
    modelType: ModelType[M],
    stockRepoFactory: StockRepoFactory[R],
    schemaCreator: SchemaCreator,
    persistenceConfig: PersistenceConfig)
  : Repo[M] = {
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
    modelType.pTypePool.filter(isPolyPType).iterator.foreach { pair => createRepoFromPair(pair) }
    modelType.pTypePool.filterNot(isPolyPType).iterator.foreach { pair => createRepoFromPair(pair) }
    val repo = new Repo[M](keyToRepoMap.widen[PRepo], schemaCreator)
    finishRepoInitialization(repo)
    autocreateSchema(repo, persistenceConfig)
    repo
  }

  private def isPolyPType(pair: TypeBoundPair[Any, TypeKey, PType, _]): Boolean = {
    pair._2.isInstanceOf[PolyPType[_]]
  }

  private def finishRepoInitialization(repo: Repo[_]): Unit = {
    repo.pRepoMap.values.foreach { pRepo => pRepo._repoOption = Some(repo) }
  }

  private def autocreateSchema(repo: Repo[_], persistenceConfig: PersistenceConfig): Unit = {
    if (persistenceConfig.autocreateSchema) {
      import scala.concurrent.Await
      import scala.concurrent.duration.Duration
      import scala.concurrent.ExecutionContext.Implicits.global
      Await.result(repo.createSchema(), Duration(10, "seconds"))
    }
  }

}
