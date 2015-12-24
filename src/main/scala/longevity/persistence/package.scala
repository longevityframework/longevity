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

  // TODO scaladoc
  implicit def rootWithTypeKey[R <: Root : TypeKey](root: R) =
    RootWithTypeKey(root, typeKey[R])

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
    }

  private def inMemRepoPool(subdomain: Subdomain): RepoPool = {
    object repoFactory extends StockRepoFactory {
      def build[R <: Root](entityType: RootType[R], entityKey: TypeKey[R]): Repo[R] =
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
      def build[R <: Root](entityType: RootType[R], entityKey: TypeKey[R]): Repo[R] =
        new MongoRepo(entityType, subdomain, mongoDB)(entityKey)
    }
    buildRepoPool(subdomain, repoFactory)
  }

  private trait StockRepoFactory {
    def build[R <: Root](entityType: RootType[R], entityKey: TypeKey[R]): Repo[R]
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

  private val emptyTypeKeyMap = TypeKeyMap[Root, Repo]

  private def finishRepoInitialization(repoPool: RepoPool): Unit = {
    repoPool.values.foreach { repo => repo._repoPoolOption = Some(repoPool) }
  }

}
