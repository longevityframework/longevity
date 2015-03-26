package longevity

import emblem._
import longevity.context._
import longevity.domain._

/** manages entity persistence operations */
package object repo {

  /** a `TypeKeyMap` of [[domain.RootEntity RootEntity]] to [[Repo]] */
  type RepoPool = TypeKeyMap[RootEntity, Repo]

  /** a function producing a specialized version of a repository given a bounded context
   * @tparam RE the root entity type for the repository
   */
  type SpecializedRepoFactory[RE <: RootEntity] = (BoundedContext) => Repo[RE]

  /** a pool of [[SpecializedRepoFactory specialized repo factories, type-mapped on the root entity type */
  type SpecializedRepoFactoryPool = TypeKeyMap[RootEntity, SpecializedRepoFactory]

  /** an empty [[SpecializedRepoFactoryPool specialized repo factory pool. used to grow larger factory pools
   * via the `+` operation
   */
  val emptySpecializedRepoFactoryPool = TypeKeyMap[RootEntity, SpecializedRepoFactory]

  /** builds and returns a [[RepoPool]] for the [[BoundedContext]]. */
  private[longevity] def repoPoolForBoundedContext(boundedContext: BoundedContext): RepoPool = {
    boundedContext.persistenceStrategy match {
      case InMem => inMemRepoPool(boundedContext, boundedContext.specializations)
      case Mongo => mongoRepoPool(boundedContext, boundedContext.specializations)
    }
  }

  /** builds and returns an in-memory [[RepoPool]] for the [[BoundedContext]]. for use in testing */
  private[longevity] def testRepoPoolForBoundedContext(boundedContext: BoundedContext): RepoPool =
    inMemRepoPool(boundedContext)

  private def inMemRepoPool(
    boundedContext: BoundedContext,
    specializations: SpecializedRepoFactoryPool = emptySpecializedRepoFactoryPool)
  : RepoPool = {
    object repoFactory extends stock.RepoFactory {
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E] =
        new InMemRepo(entityType, boundedContext)(entityKey)
    }
    buildRepoPool(boundedContext, repoFactory, specializations)
  }

  private def mongoRepoPool(
    boundedContext: BoundedContext,
    specializations: SpecializedRepoFactoryPool): RepoPool = {
    object repoFactory extends stock.RepoFactory {
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E] =
        new MongoRepo(entityType, boundedContext)(entityKey)
    }
    buildRepoPool(boundedContext, repoFactory, boundedContext.specializations)
  }

  // RepoFactory is inside object stock to prevent lint warning about declaring classes in package objects
  private object stock {
    trait RepoFactory {
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E]
    }
  }

  private def buildRepoPool(
    boundedContext: BoundedContext,
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
        case Some(specializedRepoFactory) => specializedRepoFactory(boundedContext)
        case None => stockRepoFactory.build(entityType, entityKey)
      }
      repoPool += (entityKey -> repo)
    }
    boundedContext.subdomain.rootEntityTypePool.iterator.foreach { pair => createRepoFromPair(pair) }
    finishRepoInitialization(repoPool)
    repoPool
  }

  private val emptyRepoPool = TypeKeyMap[RootEntity, Repo]

  private def finishRepoInitialization(repoPool: RepoPool): Unit = {
    repoPool.values.foreach { repo => repo._repoPoolOption = Some(repoPool) }
  }

}
