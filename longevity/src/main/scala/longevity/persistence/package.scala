package longevity

import emblem._
import longevity.context._
import longevity.subdomain._

/** manages entity persistence operations */
package object persistence {

  /** a `TypeKeyMap` of [[domain.RootEntity RootEntity]] to [[Repo]] */
  type RepoPool = TypeKeyMap[RootEntity, Repo]

  /** a function producing a specialized version of a repository given a longevity context
   * @tparam RE the root entity type for the repository
   */
  type SpecializedRepoFactory[RE <: RootEntity] = (LongevityContext) => Repo[RE]

  /** a pool of [[SpecializedRepoFactory specialized repo factories, type-mapped on the root entity type */
  type SpecializedRepoFactoryPool = TypeKeyMap[RootEntity, SpecializedRepoFactory]

  /** an empty [[SpecializedRepoFactoryPool specialized repo factory pool. used to grow larger factory pools
   * via the `+` operation
   */
  val emptySpecializedRepoFactoryPool = TypeKeyMap[RootEntity, SpecializedRepoFactory]

  /** builds and returns a [[RepoPool]] for the [[LongevityContext]]. */
  private[longevity] def repoPoolForLongevityContext(longevityContext: LongevityContext): RepoPool = {
    longevityContext.persistenceStrategy match {
      case InMem => inMemRepoPool(longevityContext, longevityContext.specializations)
      case Mongo => mongoRepoPool(longevityContext, longevityContext.specializations)
    }
  }

  /** builds and returns an in-memory [[RepoPool]] for the [[LongevityContext]]. for use in testing */
  private[longevity] def testRepoPoolForLongevityContext(longevityContext: LongevityContext): RepoPool =
    inMemRepoPool(longevityContext)

  private def inMemRepoPool(
    longevityContext: LongevityContext,
    specializations: SpecializedRepoFactoryPool = emptySpecializedRepoFactoryPool)
  : RepoPool = {
    object repoFactory extends stock.RepoFactory {
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E] =
        new InMemRepo(entityType, longevityContext)(entityKey)
    }
    buildRepoPool(longevityContext, repoFactory, specializations)
  }

  private def mongoRepoPool(
    longevityContext: LongevityContext,
    specializations: SpecializedRepoFactoryPool): RepoPool = {
    object repoFactory extends stock.RepoFactory {
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E] =
        new MongoRepo(entityType, longevityContext)(entityKey)
    }
    buildRepoPool(longevityContext, repoFactory, longevityContext.specializations)
  }

  // RepoFactory is inside object stock to prevent lint warning about declaring classes in package objects
  private object stock {
    trait RepoFactory {
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E]
    }
  }

  private def buildRepoPool(
    longevityContext: LongevityContext,
    stockRepoFactory: stock.RepoFactory,
    specializations: SpecializedRepoFactoryPool)
  : RepoPool = {
    var repoPool = emptyRepoPool
    def createRepoFromPair[
      E <: RootEntity](
      pair: TypeBoundPair[RootEntity, TypeKey, RootEntityType, E])
    : Unit = {
      val entityKey = pair._1
      val entityType = pair._2
      val repo = specializations.get(entityKey) match {
        case Some(specializedRepoFactory) => specializedRepoFactory(longevityContext)
        case None => stockRepoFactory.build(entityType, entityKey)
      }
      repoPool += (entityKey -> repo)
    }
    longevityContext.rootEntityTypePool.iterator.foreach { pair => createRepoFromPair(pair) }
    finishRepoInitialization(repoPool)
    repoPool
  }

  private val emptyRepoPool = TypeKeyMap[RootEntity, Repo]

  private def finishRepoInitialization(repoPool: RepoPool): Unit = {
    repoPool.values.foreach { repo => repo._repoPoolOption = Some(repoPool) }
  }

}
