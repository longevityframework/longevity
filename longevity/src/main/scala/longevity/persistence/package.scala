package longevity

import com.mongodb.casbah.Imports._
import com.typesafe.config.Config
import emblem.imports._
import emblem.TypeBoundPair
import longevity.context._
import longevity.persistence.mongo.MongoRepo
import longevity.subdomain._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/** manages entity persistence operations */
package object persistence {

  /** a `TypeKeyMap` of [[longevity.subdomain.RootEntity RootEntity]] to [[Repo]] */
  type RepoPool = TypeKeyMap[RootEntity, Repo]

  /** the persistent state of the entity. functionally equivalent to [[PersistentState]] */
  type PState[E <: RootEntity] = PersistentState[E]

  /** a future persistent state */
  type FPState[E <: RootEntity] = Future[PState[E]]

  /** extension methods for an [[FPState]] */
  implicit class LiftFPState[E <: RootEntity](fpState: FPState[E]) {

    /** map the future PState by mapping the root inside the PState */
    def mapRoot(f: E => E): FPState[E] =
      fpState.map { pState => pState.map { root => f(root) } }

    /** flatMap the future PState by mapping the root inside the PState into a `Future[Root]` */
    def flatMapRoot(f: E => Future[E]): FPState[E] =
      fpState.flatMap { pState => f(pState.get) map { root => pState.set(root) } }

  }

  private[longevity] def buildRepoPool(
    subdomain: Subdomain,
    persistenceStrategy: PersistenceStrategy,
    config: Config)
  : RepoPool =
    persistenceStrategy match {
      case InMem => inMemRepoPool(subdomain)
      case Mongo => mongoRepoPool(subdomain, mongoDb(config))
    }

  private def inMemRepoPool(subdomain: Subdomain): RepoPool = {
    object repoFactory extends StockRepoFactory {
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E] =
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
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E] =
        new MongoRepo(entityType, subdomain, mongoDB)(entityKey)
    }
    buildRepoPool(subdomain, repoFactory)
  }

  private trait StockRepoFactory {
    def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E]
  }

  private def buildRepoPool(
    subdomain: Subdomain,
    stockRepoFactory: StockRepoFactory)
  : RepoPool = {
    var repoPool = emptyRepoPool
    type Pair[RE <: RootEntity] = TypeBoundPair[RootEntity, TypeKey, RootEntityType, RE]
    def createRepoFromPair[RE <: RootEntity](pair: Pair[RE]): Unit = {
      val entityKey = pair._1
      val entityType = pair._2
      val repo = stockRepoFactory.build(entityType, entityKey)
      repoPool += (entityKey -> repo)
    }
    subdomain.rootEntityTypePool.iterator.foreach { pair => createRepoFromPair(pair) }
    finishRepoInitialization(repoPool)
    repoPool
  }

  private val emptyRepoPool = TypeKeyMap[RootEntity, Repo]

  private def finishRepoInitialization(repoPool: RepoPool): Unit = {
    repoPool.values.foreach { repo => repo._repoPoolOption = Some(repoPool) }
  }

}
