package longevity.repo

import scala.reflect.ClassTag

import longevity.domain._

object RepoPool {

  class MultipleReposForEntityType[E <: Entity](val repo1: Repo[E], val repo2: Repo[E])
  extends Exception(s"multiple repos for entity type ${repo1.entityClassTag}: $repo1 and $repo2")

  class NoRepoForEntityType[E <: Entity](val entityClassTag: ClassTag[E])
  extends Exception(s"no repo for entity type $entityClassTag found in the pool")

}

/** maintains a pool of all the repositories in use. */
class RepoPool {

  private var entityClassTagToRepo = Map[ClassTag[_], Repo[_]]()

  /** adds a repo to the repo pool for entity type E. */
  @throws[RepoPool.MultipleReposForEntityType[_]]
  private[repo] def addRepo[E <: Entity](repo: Repo[E]): Unit = {
    val entityClassTag = repo.entityClassTag
    if (entityClassTagToRepo.contains(entityClassTag)) {
      throw new RepoPool.MultipleReposForEntityType(tagToRepo(entityClassTag), repo)
    }
    entityClassTagToRepo += (entityClassTag -> repo)
  }

  // TODO not currently used
  @throws[RepoPool.NoRepoForEntityType[_]]
  //private[repo] 
  def repoForEntity[E <: Entity](e: E)(implicit classTag: ClassTag[E]): Repo[E] = {
    repoForEntityClassTag(classTag)
  }

  @throws[RepoPool.NoRepoForEntityType[_]]
  //private[repo] 
  def repoForEntityClassTag[E <: Entity](entityClassTag: ClassTag[E]): Repo[E] = {
    if (!entityClassTagToRepo.contains(entityClassTag)) {
      throw new RepoPool.NoRepoForEntityType(entityClassTag)
    }
    tagToRepo(entityClassTag)
  }

  private def tagToRepo[E <: Entity](entityClassTag: ClassTag[E]): Repo[E] =
    entityClassTagToRepo(entityClassTag).asInstanceOf[Repo[E]]

}
