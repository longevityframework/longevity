package longevity.repo

import longevity.domain._

object RepoPool {

  class MultipleReposForEntityType[E <: Entity](val repo1: Repo[E], val repo2: Repo[E])
  extends Exception(s"multiple repos for entity type $repo1.entityType: $repo1 and $repo2")

  class NoRepoForEntityType[E <: Entity](val entityType: EntityType[E])
  extends Exception(s"no repo for entity type $entityType found in the pool")

}

/** maintains a pool of all the repositories in use. */
class RepoPool {

  private var entityTypeToRepo = Map[EntityType[_], Repo[_]]()

  /** adds a repo to the repo pool for entity type E. */
  @throws[RepoPool.MultipleReposForEntityType[_]]
  def addRepo[E <: Entity](repo: Repo[E]): Unit = {
    val entityType = repo.entityType
    if (entityTypeToRepo.contains(entityType)) {
      throw new RepoPool.MultipleReposForEntityType(typeToRepo(entityType), repo)
    }
    entityTypeToRepo += (entityType -> repo)
  }

  @throws[RepoPool.NoRepoForEntityType[_]]
  def repoForEntityType[E <: Entity](entityType: EntityType[E]): Repo[E] = {
    if (!entityTypeToRepo.contains(entityType)) {
      throw new RepoPool.NoRepoForEntityType(entityType)
    }
    typeToRepo(entityType)
  }

  private def typeToRepo[E <: Entity](entityType: EntityType[E]): Repo[E] =
    entityTypeToRepo(entityType).asInstanceOf[Repo[E]]

}
