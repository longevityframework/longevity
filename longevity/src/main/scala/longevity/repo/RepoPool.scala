package longevity.repo

import emblem._
import longevity.domain._

// TODO s/ypeTag/ypeKey/
// TODO: use TypedKeyMap here

object RepoPool {

  // TODO: move these exceptions to the exceptions package

  class MultipleReposForEntityType[E <: Entity](val repo1: Repo[E], val repo2: Repo[E])
  extends Exception(s"multiple repos for entity type ${repo1.entityTypeKey.tag}: $repo1 and $repo2")

  class NoRepoForEntityType[E <: Entity](val entityTypeKey: TypeKey[E])
  extends Exception(s"no repo for entity type ${entityTypeKey.tag} found in the pool")

}

/** maintains a pool of all the repositories in use. */
class RepoPool {

  private var entityTypeKeyToRepo = Map[TypeKey[_], Repo[_]]()

  // TODO: replace this
  def entityTypeKeys = entityTypeKeyToRepo.keys.asInstanceOf[Set[TypeKey[_ <: Entity]]]

  /** adds a repo to the repo pool for entity type E. */
  @throws[RepoPool.MultipleReposForEntityType[_]]
  private[repo] def addRepo[E <: Entity](repo: Repo[E]): Unit = {
    val entityTypeKey = repo.entityTypeKey
    if (entityTypeKeyToRepo.contains(entityTypeKey)) {
      throw new RepoPool.MultipleReposForEntityType(keyToRepo(entityTypeKey), repo)
    }
    entityTypeKeyToRepo += (entityTypeKey -> repo)
  }

  @throws[RepoPool.NoRepoForEntityType[_]]
  // TODO reinstate private
  //private[repo] 
  def repoForEntityTypeKey[E <: Entity](entityTypeKey: TypeKey[E]): Repo[E] = {
    if (!entityTypeKeyToRepo.contains(entityTypeKey)) {
      throw new RepoPool.NoRepoForEntityType(entityTypeKey)
    }
    keyToRepo(entityTypeKey)
  }

  private def keyToRepo[E <: Entity](entityTypeKey: TypeKey[E]): Repo[E] =
    entityTypeKeyToRepo(entityTypeKey).asInstanceOf[Repo[E]]

}
