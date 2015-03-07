package longevity

import emblem._
import domain._

package object repo {

  /** A [[TypeKeyMap]] of [[Entity]] to [[Repo]] */
  type RepoPool = TypeKeyMap[Entity, Repo]  

  // TODO: docs for all this new stuff

  type SpecializedRepoFactory[E <: Entity] = (EntityType[E]) => Repo[E]

  type SpecializedRepoFactoryPool = TypeKeyMap[Entity, SpecializedRepoFactory]

  object SpecializedRepoFactoryPool {
    def apply(): SpecializedRepoFactoryPool = TypeKeyMap[Entity, SpecializedRepoFactory]
  }

  def inMemRepoPoolForBoundedCountext(
    boundedContext: BoundedContext,
    specializations: SpecializedRepoFactoryPool = SpecializedRepoFactoryPool())
  : RepoPool = {
    object repoFactory extends stock.RepoFactory {
      def build[E <: Entity](entityType: EntityType[E], entityKey: TypeKey[E]): Repo[E] =
        new InMemRepo(entityType)(entityKey)
    }
    repoPoolForBoundedCountext(boundedContext, repoFactory, specializations)
  }

  def mongoRepoPoolForBoundedCountext(
    boundedContext: BoundedContext,
    specializations: SpecializedRepoFactoryPool = SpecializedRepoFactoryPool())
  : RepoPool = {
    object repoFactory extends stock.RepoFactory {
      def build[E <: Entity](entityType: EntityType[E], entityKey: TypeKey[E]): Repo[E] =
        new MongoRepo(entityType, boundedContext.shorthandPool)(entityKey)
    }
    repoPoolForBoundedCountext(boundedContext, repoFactory, specializations)
  }

  // this is private because we build the repo pool for you
  private object RepoPool {
    def apply(): RepoPool = TypeKeyMap[Entity, Repo]
  }

  // RepoFactory is inside object stock to prevent lint warning about declaring classes in package objects
  private object stock {
    trait RepoFactory {
      def build[E <: Entity](entityType: EntityType[E], entityKey: TypeKey[E]): Repo[E]
    }
  }

  private def repoPoolForBoundedCountext(
    boundedContext: BoundedContext,
    stockRepoFactory: stock.RepoFactory,
    specializations: SpecializedRepoFactoryPool)
  : RepoPool = {
    var repoPool = RepoPool()
    def createRepoFromPair[E <: Entity](pair: TypeBoundPair[Entity, TypeKey, EntityType, E]): Unit = {
      val entityKey = pair._1
      val entityType = pair._2
      val repo = specializations.get(entityKey) match {
        case Some(specRepoGen) => specRepoGen(entityType)
        case None => stockRepoFactory.build(entityType, entityKey)
      }
      repoPool += (entityKey -> repo)
    }
    boundedContext.entityTypePool.iterator.foreach { pair => createRepoFromPair(pair) }
    finishRepoInitialization(repoPool)
    repoPool
  }

  private def finishRepoInitialization(repoPool: RepoPool): Unit = {
    repoPool.values.foreach { repo => repo._repoPoolOption = Some(repoPool) }
  }

}
