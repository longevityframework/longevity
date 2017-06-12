package longevity.persistence

import typekey.TypeKey
import typekey.TypeKeyMap
import typekey.TypeBoundPair
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

  private trait StockRepoFactory[M, R[M, P] <: PRepo[M, P]] {
    def build[P](
      pType: PType[M, P],
      polyRepoOpt: Option[R[M, _ >: P]] = None)
    : R[M, P]
  }

  private def cassandraRepo[M](
    modelType: ModelType[M],
    session: CassandraSessionInfo,
    persistenceConfig: PersistenceConfig)
  : Repo[M] = {
    object repoFactory extends StockRepoFactory[M, CassandraRepo] {
      def build[P](
        pType: PType[M, P],
        polyRepoOpt: Option[CassandraRepo[M, _ >: P]])
      : CassandraRepo[M, P] =
        CassandraRepo[M, P](pType, modelType, session, persistenceConfig, polyRepoOpt)
    }
    buildRepo(modelType, repoFactory, session, persistenceConfig)
  }

  private def inMemTestRepo[M](modelType: ModelType[M], persistenceConfig: PersistenceConfig): Repo[M] = {
    object repoFactory extends StockRepoFactory[M, InMemRepo] {
      def build[P](
        pType: PType[M, P],
        polyRepoOpt: Option[InMemRepo[M, _ >: P]])
      : InMemRepo[M, P] =
        InMemRepo[M, P](pType, modelType, persistenceConfig, polyRepoOpt)
    }
    buildRepo(modelType, repoFactory, SchemaCreator.empty, persistenceConfig)
  }

  private def mongoRepo[M](
    modelType: ModelType[M],
    session: MongoSessionInfo,
    persistenceConfig: PersistenceConfig)
  : Repo[M] = {
    object repoFactory extends StockRepoFactory[M, MongoRepo] {
      def build[P](
        pType: PType[M, P],
        polyRepoOpt: Option[MongoRepo[M, _ >: P]])
      : MongoRepo[M, P] =
        MongoRepo[M, P](pType, modelType, session, persistenceConfig, polyRepoOpt)
    }
    buildRepo(modelType, repoFactory, SchemaCreator.empty, persistenceConfig)
  }

  private def sqliteRepo[M](
    modelType: ModelType[M],
    session: JdbcSessionInfo,
    persistenceConfig: PersistenceConfig)
  : Repo[M] = {
    object repoFactory extends StockRepoFactory[M, SQLiteRepo] {
      def build[P](
        pType: PType[M, P],
        polyRepoOpt: Option[SQLiteRepo[M, _ >: P]])
      : SQLiteRepo[M, P] =
        SQLiteRepo[M, P](pType, modelType, session, persistenceConfig, polyRepoOpt)
    }
    buildRepo(modelType, repoFactory, SchemaCreator.empty, persistenceConfig)
  }

  private def jdbcRepo[M](
    modelType: ModelType[M],
    session: JdbcSessionInfo,
    persistenceConfig: PersistenceConfig)
  : Repo[M] = {
    object repoFactory extends StockRepoFactory[M, JdbcRepo] {
      def build[P](
        pType: PType[M, P],
        polyRepoOpt: Option[JdbcRepo[M, _ >: P]])
      : JdbcRepo[M, P] =
        JdbcRepo[M, P](pType, modelType, session, persistenceConfig, polyRepoOpt)
    }
    buildRepo(modelType, repoFactory, SchemaCreator.empty, persistenceConfig)
  }

  private def buildRepo[M, R[M, P] <: PRepo[M, P]](
    modelType: ModelType[M],
    stockRepoFactory: StockRepoFactory[M, R],
    schemaCreator: SchemaCreator,
    persistenceConfig: PersistenceConfig)
  : Repo[M] = {
    var keyToRepoMap = TypeKeyMap[Any, R[M, ?]]
    type Pair[P] = TypeBoundPair[Any, TypeKey, PType[M, ?], P]
    def createRepoFromPair[P](pair: Pair[P]): Unit = {
      val pTypeKey = pair._1
      val pType = pair._2

      val polyKey: Option[TypeKey[_ >: P]] = pType match {
        case dpt: DerivedPType[_, _, _] => Some(dpt.polyPTypeKey)
        case _ => None
      }

      val repo = stockRepoFactory.build[P](pType, polyKey.map(keyToRepoMap(_)))
      keyToRepoMap += (pTypeKey -> repo)

    }

    type PTypeM[P] = PType[M, P]

    def isPolyPType(pair: TypeBoundPair[Any, TypeKey, PTypeM, _]): Boolean = {
      pair._2.isInstanceOf[PolyPType[_, _]]
    }

    // do PolyPTypes first
    modelType.pTypePool.filter(isPolyPType).iterator.foreach { pair => createRepoFromPair(pair) }
    modelType.pTypePool.filterNot(isPolyPType).iterator.foreach { pair => createRepoFromPair(pair) }

    type PRepoM[P] = PRepo[M, P]

    val repo = new Repo[M](schemaCreator) {
      override private[persistence] val pRepoMap = keyToRepoMap.widen[PRepoM]
    }

    // finish repo initialization
    repo.pRepoMap.values.foreach { pRepo => pRepo._repoOption = Some(repo) }

    autocreateSchema(repo, persistenceConfig)
    repo
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
