package longevity.persistence

import com.datastax.driver.core.Session
import com.mongodb.casbah.MongoDB
import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeBound.TypeBoundPair
import longevity.context.Cassandra
import longevity.context.InMem
import longevity.context.Mongo
import longevity.context.PersistenceConfig
import longevity.context.PersistenceStrategy
import longevity.context.LongevityConfig
import longevity.persistence.cassandra.CassandraRepo
import longevity.persistence.inmem.InMemRepo
import longevity.persistence.mongo.MongoRepo
import longevity.subdomain.ptype.DerivedPType
import longevity.subdomain.ptype.PolyPType
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.PType

/** builds repo pools for LongevityContextImpl */
private[longevity] object RepoPoolBuilder {

  private[longevity] def buildRepoPool(
    subdomain: Subdomain,
    persistenceStrategy: PersistenceStrategy,
    config: LongevityConfig,
    test: Boolean)
  : RepoPool =
    persistenceStrategy match {
      case InMem =>
        inMemTestRepoPool(subdomain, config)
      case Mongo =>
        val mongoConfig = if (test) config.test.mongodb else config.mongodb
        mongoRepoPool(subdomain, MongoRepo.mongoDbFromConfig(mongoConfig), config)
      case Cassandra =>
        val cassandraConfig = if (test) config.test.cassandra else config.cassandra
        cassandraRepoPool(subdomain, CassandraRepo.sessionFromConfig(cassandraConfig), config)
    }

  private trait StockRepoFactory[R[P <: Persistent] <: BaseRepo[P]] {
    def build[P <: Persistent](
      pType: PType[P],
      polyRepoOpt: Option[R[_ >: P <: Persistent]] = None)
    : R[P]
  }

  private def inMemTestRepoPool(subdomain: Subdomain, persistenceConfig: PersistenceConfig): RepoPool = {
    object repoFactory extends StockRepoFactory[InMemRepo] {
      def build[P <: Persistent](
        pType: PType[P],
        polyRepoOpt: Option[InMemRepo[_ >: P <: Persistent]])
      : InMemRepo[P] =
        InMemRepo[P](pType, subdomain, persistenceConfig, polyRepoOpt)
    }
    buildRepoPool(subdomain, repoFactory)
  }

  private def mongoRepoPool(
    subdomain: Subdomain,
    mongoDB: MongoDB,
    persistenceConfig: PersistenceConfig)
  : RepoPool = {
    object repoFactory extends StockRepoFactory[MongoRepo] {
      def build[P <: Persistent](
        pType: PType[P],
        polyRepoOpt: Option[MongoRepo[_ >: P <: Persistent]])
      : MongoRepo[P] =
        MongoRepo[P](pType, subdomain, mongoDB, polyRepoOpt)
    }
    buildRepoPool(subdomain, repoFactory)
  }

  private def cassandraRepoPool(
    subdomain: Subdomain,
    session: Session,
    persistenceConfig: PersistenceConfig)
  : RepoPool = {
    object repoFactory extends StockRepoFactory[CassandraRepo] {
      def build[P <: Persistent](
        pType: PType[P],
        polyRepoOpt: Option[CassandraRepo[_ >: P <: Persistent]])
      : CassandraRepo[P] =
        CassandraRepo[P](pType, subdomain, session, polyRepoOpt)
    }
    buildRepoPool(subdomain, repoFactory)
  }

  private def buildRepoPool[R[P <: Persistent] <: BaseRepo[P]](
    subdomain: Subdomain,
    stockRepoFactory: StockRepoFactory[R])
  : RepoPool = {
    var keyToRepoMap = TypeKeyMap[Persistent, R]
    type Pair[P <: Persistent] = TypeBoundPair[Persistent, TypeKey, PType, P]
    def createRepoFromPair[P <: Persistent](pair: Pair[P]): Unit = {
      val pTypeKey = pair._1
      val pType = pair._2

      val polyKey: Option[TypeKey[_ >: P <: Persistent]] = pType match {
        case dpt: DerivedPType[_, _] => Some(dpt.polyPType.pTypeKey)
        case _ => None
      }

      val repo = stockRepoFactory.build[P](pType, polyKey.map(keyToRepoMap(_)))
      keyToRepoMap += (pTypeKey -> repo)

    }
    subdomain.pTypePool.filter(isPolyPType).iterator.foreach { pair => createRepoFromPair(pair) }
    subdomain.pTypePool.filterNot(isPolyPType).iterator.foreach { pair => createRepoFromPair(pair) }
    val repoPool = new RepoPool(keyToRepoMap.widen[BaseRepo])
    finishRepoInitialization(repoPool)
    repoPool
  }

  private def isPolyPType(pair: TypeBoundPair[Persistent, TypeKey, PType, _ <: Persistent]): Boolean = {
    pair._2.isInstanceOf[PolyPType[_]]
  }

  private def finishRepoInitialization(repoPool: RepoPool): Unit = {
    repoPool.baseRepoMap.values.foreach { repo => repo._repoPoolOption = Some(repoPool) }
  }

}
