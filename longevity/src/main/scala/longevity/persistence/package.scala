package longevity

import emblem.imports._
import emblem.TypeBoundPair
import longevity.shorthands._
import longevity.subdomain._

/** manages entity persistence operations */
package object persistence {

  /** a `TypeKeyMap` of [[longevity.subdomain.RootEntity RootEntity]] to [[Repo]] */
  type RepoPool = TypeKeyMap[RootEntity, Repo]

  /** a function producing a specialized version of a repository given a longevity context
   * @tparam RE the root entity type for the repository
   */
  type SpecializedRepoFactory[RE <: RootEntity] = (EmblemPool, ShorthandPool) => Repo[RE]

  /** a pool of [[SpecializedRepoFactory specialized repo factories, type-mapped on the root entity type */
  type SpecializedRepoFactoryPool = TypeKeyMap[RootEntity, SpecializedRepoFactory]

  object SpecializedRepoFactoryPool {
    
    /** an empty [[SpecializedRepoFactoryPool specialized repo factory pool. used to grow larger factory pools
     * via the `+` operation
     */
    val empty: SpecializedRepoFactoryPool = TypeKeyMap[RootEntity, SpecializedRepoFactory]
  }

  private[longevity] def buildRepoPool(
    subdomain: Subdomain,
    shorthandPool: ShorthandPool,
    persistenceStrategy: PersistenceStrategy,
    specializedRepoFactoryPool: SpecializedRepoFactoryPool = SpecializedRepoFactoryPool.empty)
  : RepoPool =
    persistenceStrategy match {
      case InMem => inMemRepoPool(subdomain, shorthandPool, specializedRepoFactoryPool)
      case Mongo => mongoRepoPool(subdomain, shorthandPool, specializedRepoFactoryPool)
    }

  private def inMemRepoPool(
    subdomain: Subdomain,
    shorthandPool: ShorthandPool,
    specializedRepoFactoryPool: SpecializedRepoFactoryPool)
  : RepoPool = {
    object repoFactory extends StockRepoFactory {
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E] =
        new InMemRepo(entityType)(entityKey)
    }
    buildRepoPool(subdomain, shorthandPool, specializedRepoFactoryPool, repoFactory)
  }

  private def mongoRepoPool(
    subdomain: Subdomain,
    shorthandPool: ShorthandPool,
    specializedRepoFactoryPool: SpecializedRepoFactoryPool)
  : RepoPool = {
    object repoFactory extends StockRepoFactory {
      def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E] =
        new MongoRepo(entityType, subdomain.entityEmblemPool, shorthandPool)(entityKey)
    }
    buildRepoPool(subdomain, shorthandPool, specializedRepoFactoryPool, repoFactory)
  }

  private trait StockRepoFactory {
    def build[E <: RootEntity](entityType: RootEntityType[E], entityKey: TypeKey[E]): Repo[E]
  }

  private def buildRepoPool(
    subdomain: Subdomain,
    shorthandPool: ShorthandPool,
    specializedRepoFactoryPool: SpecializedRepoFactoryPool,
    stockRepoFactory: StockRepoFactory)
  : RepoPool = {
    var repoPool = emptyRepoPool
    type Pair[RE <: RootEntity] = TypeBoundPair[RootEntity, TypeKey, RootEntityType, RE]
    def createRepoFromPair[RE <: RootEntity](pair: Pair[RE]): Unit = {
      val entityKey = pair._1
      val entityType = pair._2
      val repo = specializedRepoFactoryPool.get(entityKey) match {
        case Some(specializedRepoFactory) =>
          specializedRepoFactory(subdomain.entityEmblemPool, shorthandPool)
        case None => stockRepoFactory.build(entityType, entityKey)
      }
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
