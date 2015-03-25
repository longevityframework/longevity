package longevity

import emblem._
import longevity.context._
import longevity.domain._

// TODO: package level scaladoc
package object repo {

  /** a `TypeKeyMap` of [[domain.RootEntity RootEntity]] to [[Repo]] */
  type RepoPool = TypeKeyMap[RootEntity, Repo]

  // TODO scaladoc for these three

  type SpecializedRepoFactory[RE <: RootEntity] = (BoundedContext) => Repo[RE]

  type SpecializedRepoFactoryPool = TypeKeyMap[RootEntity, SpecializedRepoFactory]

  val emptySpecializedRepoFactoryPool = TypeKeyMap[RootEntity, SpecializedRepoFactory]

  /** builds and returns a [[RepoPool]] for the [[BoundedContext]]. */
  private[longevity] def repoPoolForBoundedContext(boundedContext: BoundedContext): RepoPool = {
    boundedContext.persistenceStrategy match {
      case InMem => inMemRepoPool(boundedContext, boundedContext.specializations)
      case Mongo => mongoRepoPool(boundedContext, boundedContext.specializations)
    }
  }

  // TODO @params for scaladocs below have fallen off

  /** builds and returns a [[RepoPool]] of [[InMemRepo in-memory repositories]] for all the root entities in
   * the subdomain. stock in-memory repositories will created, except where specialized versions are
   * provided.
   * @param subdomain the bounded context
   * @param specializations specialized repositories to include in the pool, in place of the stock
   * in-memory repositories
   */
  private[longevity] def inMemRepoPool(
    boundedContext: BoundedContext,
    specializations: SpecializedRepoFactoryPool = emptySpecializedRepoFactoryPool)
  : RepoPool = {
    object repoFactory extends stock.RepoFactory {
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E] =
        new InMemRepo(entityType, boundedContext)(entityKey)
    }
    buildRepoPool(boundedContext, repoFactory, specializations)
  }

  /** builds and returns a [[RepoPool]] of [[MongoRepo mongo repositories]] for all the root entities in the
   * subdomain. stock mongo repositories will created, except where specialized versions are
   * provided.
   * @param boundedContext the bounded context
   */
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

  // this is private because longevity takes responsibility for building the repo pools
  private val emptyRepoPool = TypeKeyMap[RootEntity, Repo]

  private def finishRepoInitialization(repoPool: RepoPool): Unit = {
    repoPool.values.foreach { repo => repo._repoPoolOption = Some(repoPool) }
  }

}
