package longevity

import com.datastax.driver.core.Session
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.MongoDB
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
import longevity.subdomain.DerivedPType
import longevity.subdomain.PolyPType
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
  // - repo behavior has to vary based on PolyType/DerivedType:
  //   - Mongo
  //   - InMem
  // - test for when Prop.propVal is accessed too early
  //
  // - search for / clean up "persistent entity" language. use "persistent element" instead?
  // - repackaging in longevity.subdomain

  private[longevity] def buildRepoPool(
    subdomain: Subdomain,
    persistenceStrategy: PersistenceStrategy,
    config: Config)
  : RepoPool =
    persistenceStrategy match {
      case InMem => inMemRepoPool(subdomain)
      case Mongo => mongoRepoPool(subdomain, mongoDb(config))
      case Cassandra => cassandraRepoPool(subdomain, CassandraRepo.sessionFromConfig(config))
    }

  private def inMemRepoPool(subdomain: Subdomain): RepoPool = {
    object repoFactory extends StockRepoFactory[InMemRepo] {
      def build[P <: Persistent](
        pType: PType[P],
        polyRepoOpt: Option[InMemRepo[_ >: P <: Persistent]])
      : InMemRepo[P] =
        new InMemRepo(pType, subdomain)
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
    object repoFactory extends StockRepoFactory[MongoRepo] {
      def build[P <: Persistent](
        pType: PType[P],
        polyRepoOpt: Option[MongoRepo[_ >: P <: Persistent]])
      : MongoRepo[P] =
        new MongoRepo(pType, subdomain, mongoDB)
    }
    buildRepoPool(subdomain, repoFactory)
  }

  private def cassandraRepoPool(subdomain: Subdomain, session: Session): RepoPool = {
    object repoFactory extends StockRepoFactory[CassandraRepo] {
      def build[P <: Persistent](
        pType: PType[P],
        // TODO have fun crashing compiler by removing <: Persistent here (and in children)
        polyRepoOpt: Option[CassandraRepo[_ >: P <: Persistent]])
      : CassandraRepo[P] =
        CassandraRepo[P](pType, subdomain, session, polyRepoOpt)
    }
    buildRepoPool(subdomain, repoFactory)
  }

  private trait StockRepoFactory[R[P <: Persistent] <: BaseRepo[P]] {
    def build[P <: Persistent](
      pType: PType[P],
      polyRepoOpt: Option[R[_ >: P <: Persistent]] = None)
    : R[P]
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

      // TODO have fun crashing the compiler by removing the "[P]" on the following line
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

  // stuff for {Repo,RepoPool}.createMany. also used by RepoCrudSpec.randomP

  private[longevity] type PIdentity[P <: Persistent] = P

  private[longevity] type CreatedCache = TypeBoundMap[Persistent, PIdentity, PState]

  private[longevity] object CreatedCache {
    def apply() = TypeBoundMap[Persistent, PIdentity, PState]()
  }

}
