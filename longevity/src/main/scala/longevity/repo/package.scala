package longevity

import emblem._
import longevity.context._
import longevity.domain._

// TODO: package level scaladoc
package object repo {

  /** a `TypeKeyMap` of [[domain.RootEntity RootEntity]] to [[Repo]] */
  type RepoPool = TypeKeyMap[RootEntity, Repo]

  /** like a [[RepoPool]], except that the [[Repo repositories]] have not yet been fully initialized */
  type ProvisionalRepoPool = TypeKeyMap[RootEntity, Repo]

  /** an empty [[ProvisionalRepoPool]] */
  val emptyProvisionalRepoPool = TypeKeyMap[RootEntity, Repo]

  /** builds and returns a [[RepoPool]] for the [[BoundedContext]]. */
  private[longevity] def repoPoolForBoundedContext(boundedContext: BoundedContext[_]): RepoPool = {
    boundedContext.persistenceStrategy match {
      case InMem => inMemRepoPool(
        boundedContext.subdomain,
        boundedContext.specializations)
      case Mongo => mongoRepoPool(
        boundedContext.subdomain,
        boundedContext.shorthandPool,
        boundedContext.specializations)
    }
  }

  /** builds and returns a [[RepoPool]] of [[InMemRepo in-memory repositories]] for all the root entities in
   * the subdomain. stock in-memory repositories will created, except where specialized versions are
   * provided.
   * @param subdomain the bounded context
   * @param specializations specialized repositories to include in the pool, in place of the stock
   * in-memory repositories
   */
  private[longevity] def inMemRepoPool(
    subdomain: Subdomain,
    specializations: ProvisionalRepoPool = emptyProvisionalRepoPool)
  : RepoPool = {
    object repoFactory extends stock.RepoFactory {
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E] =
        new InMemRepo(entityType)(entityKey)
    }
    buildRepoPool(subdomain, repoFactory, specializations)
  }

  /** builds and returns a [[RepoPool]] of [[MongoRepo mongo repositories]] for all the root entities in the
   * subdomain. stock mongo repositories will created, except where specialized versions are
   * provided.
   * @param subdomain the bounded context
   * @param shorthandPool the shorthands to use when converting to/from BSON
   * @param specializations specialized repositories to include in the pool, in place of the stock
   * mongo repositories
   */
  private def mongoRepoPool(
    subdomain: Subdomain,
    shorthandPool: ShorthandPool = ShorthandPool(),
    specializations: ProvisionalRepoPool = emptyProvisionalRepoPool)
  : RepoPool = {
    object repoFactory extends stock.RepoFactory {
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E] =
        new MongoRepo(entityType, shorthandPool)(entityKey)
    }
    buildRepoPool(subdomain, repoFactory, specializations)
  }

  // RepoFactory is inside object stock to prevent lint warning about declaring classes in package objects
  private object stock {
    trait RepoFactory {
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E]
    }
  }

  private def buildRepoPool(
    subdomain: Subdomain,
    stockRepoFactory: stock.RepoFactory,
    specializations: ProvisionalRepoPool)
  : RepoPool = {
    var repoPool = emptyRepoPool
    def createRepoFromPair[
      E <: RootEntity](
      pair: TypeBoundPair[RootEntity, TypeKey, RootEntityType, E])
    : Unit = {
      val entityKey = pair._1
      val entityType = pair._2
      val repo = specializations.get(entityKey) match {
        case Some(repo) => repo
        case None => stockRepoFactory.build(entityType, entityKey)
      }
      repoPool += (entityKey -> repo)
    }
    subdomain.rootEntityTypePool.iterator.foreach { pair => createRepoFromPair(pair) }
    finishRepoInitialization(repoPool)
    repoPool
  }

  // this is private because longevity takes responsibility for building the repo pools
  private val emptyRepoPool = TypeKeyMap[RootEntity, Repo]

  private def finishRepoInitialization(repoPool: RepoPool): Unit = {
    repoPool.values.foreach { repo => repo._repoPoolOption = Some(repoPool) }
  }

}
