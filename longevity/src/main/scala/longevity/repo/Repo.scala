package longevity.repo

import longevity.domain._

// TODO: these methods should all return futures

trait Repo[E <: Entity] {

  /** the type object for the entities this repository handles */
  val entityType: EntityType[E]

  /** the pool of all the repos in context */
  protected val repoPool: RepoPool

  repoPool.addRepo(this)

  /** creates the entity */
  def create(e: Unpersisted[E]): CreateResult[E]

  /** retrieves the entity by id */
  def retrieve(id: Id[E]): RetrieveResult[E]

  /** updates the entity */
  def update(p: Persisted[E]): UpdateResult[E]

  /** deletes the entity */
  def delete(p: Persisted[E]): DeleteResult[E]

}
