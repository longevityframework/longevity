package longevity.repo

import scala.reflect.runtime.universe.TypeTag
//import emblem._
import longevity.domain._

// TODO s/ypeTag/ypeKey/
// TODO: use TypedKeyMap here

object RepoPool {

  class MultipleReposForEntityType[E <: Entity](val repo1: Repo[E], val repo2: Repo[E])
  extends Exception(s"multiple repos for entity type ${repo1.entityTypeKey.tag}: $repo1 and $repo2")

  class NoRepoForEntityType[E <: Entity](val entityTypeTag: TypeTag[E])
  extends Exception(s"no repo for entity type $entityTypeTag found in the pool")

}

/** maintains a pool of all the repositories in use. */
class RepoPool {

  private var entityTypeTagToRepo = Map[TypeTag[_], Repo[_]]()

  // TODO: replace this
  def entityTypeTags = entityTypeTagToRepo.keys.asInstanceOf[Set[TypeTag[_ <: Entity]]]

  /** adds a repo to the repo pool for entity type E. */
  @throws[RepoPool.MultipleReposForEntityType[_]]
  private[repo] def addRepo[E <: Entity](repo: Repo[E]): Unit = {
    val entityTypeTag = repo.entityTypeKey.tag
    if (entityTypeTagToRepo.contains(entityTypeTag)) {
      throw new RepoPool.MultipleReposForEntityType(tagToRepo(entityTypeTag), repo)
    }
    entityTypeTagToRepo += (entityTypeTag -> repo)
  }

  @throws[RepoPool.NoRepoForEntityType[_]]
  // TODO reinstate private
  //private[repo] 
  def repoForEntityTypeTag[E <: Entity](entityTypeTag: TypeTag[E]): Repo[E] = {
    if (!entityTypeTagToRepo.contains(entityTypeTag)) {
      throw new RepoPool.NoRepoForEntityType(entityTypeTag)
    }
    tagToRepo(entityTypeTag)
  }

  private def tagToRepo[E <: Entity](entityTypeTag: TypeTag[E]): Repo[E] =
    entityTypeTagToRepo(entityTypeTag).asInstanceOf[Repo[E]]

}
