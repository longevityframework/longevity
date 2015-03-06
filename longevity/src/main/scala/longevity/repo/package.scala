package longevity

import emblem._
import domain._

package object repo {

  /** A [[TypeKeyMap]] of [[Entity]] to [[Repo]] */
  type RepoPool = TypeKeyMap[Entity, Repo]  

  private object RepoPool {
    def apply(): RepoPool = TypeKeyMap[Entity, Repo]
  }

  // TODO: docs for all this new stuff

  type SpecializedRepoGenerator[E <: Entity] = (EntityType[E]) => Repo[E]

  type SpecializedRepoGeneratorPool = TypeKeyMap[Entity, SpecializedRepoGenerator]

  object SpecializedRepoGeneratorPool {
    def apply(): SpecializedRepoGeneratorPool = TypeKeyMap[Entity, SpecializedRepoGenerator]
  }

  // TODO: remove code duplication in next two methods

  def inMemRepoPoolForBoundedCountext(
    boundedContext: BoundedContext,
    specializations: SpecializedRepoGeneratorPool = SpecializedRepoGeneratorPool())
  : RepoPool = {
    var repoPool = RepoPool()
    def createRepoFromPair[E <: Entity](pair: TypeBoundPair[Entity, TypeKey, EntityType, E]): Unit = {
      val entityKey = pair._1
      val entityType = pair._2
      val repo = specializations.get(entityKey) match {
        case Some(specRepoGen) => specRepoGen(entityType)
        case None => new InMemRepo(entityType)(entityKey)
      }
      repoPool += (entityKey -> repo)
    }
    boundedContext.entityTypePool.iterator.foreach { pair => createRepoFromPair(pair) }
    finishRepoInitialization(repoPool)
    repoPool
  }

  def mongoRepoPoolForBoundedCountext(
    boundedContext: BoundedContext,
    specializations: TypeKeyMap[Entity, SpecializedRepoGenerator] = TypeKeyMap[Entity, SpecializedRepoGenerator])
  : RepoPool = {
    var repoPool = RepoPool()
    def createRepoFromPair[E <: Entity](pair: TypeBoundPair[Entity, TypeKey, EntityType, E]): Unit = {
      val entityKey = pair._1
      val entityType = pair._2
      val repo = specializations.get(entityKey) match {
        case Some(specRepoGen) => specRepoGen(entityType)
        case None => new MongoRepo(entityType, boundedContext.shorthandPool)(entityKey)
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
