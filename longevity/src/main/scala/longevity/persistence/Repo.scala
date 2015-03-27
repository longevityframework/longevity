package longevity.persistence

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import emblem._
import longevity.subdomain._

/** a repository for aggregate roots of type E
 * @param repoPool the pool of all the repos in the longevity context
 */
abstract class Repo[E <: RootEntity : TypeKey] {

  private[persistence] var _repoPoolOption: Option[RepoPool] = None

  /** the type key for the aggregate roots this repository handles */
  val entityTypeKey: TypeKey[E] = typeKey[E]

  /** the entity type for the aggregate roots this repository handles */
  val entityType: EntityType[E]

  /** creates the aggregate */
  def create(e: Unpersisted[E]): Future[Persisted[E]]

  /** convenience method for creating the aggregate */
  def create(e: E): Future[Persisted[E]] = create(Unpersisted(e))

  /** retrieves the aggregate by id */
  def retrieve(id: PersistedAssoc[E]): Future[Option[Persisted[E]]]

  /** updates the aggregate */
  def update(p: Persisted[E]): Future[Persisted[E]]

  /** deletes the aggregate */
  def delete(p: Persisted[E]): Future[Deleted[E]]

  /** the pool of all the repos for the [[longevity.subdomain.Subdomain longevity context]].
   *
   * PLEASE NOTE that the repo pool is only available for use after all the repositories in the pool have
   * been initialized. if you attempt to access the pool during the initialization of your customized
   * repository, you will get a NoSuchElementException.
   */
  protected lazy val repoPool: RepoPool = _repoPoolOption.get

  /** a cache of create results for those unpersisted entities of type E that have already been created.
   * because entities are just value objects, we expect some duplication in the unpersisted data that gets
   * passed into `Repo.create`, via the associations of created obects. we keep a session
   * level cache of these guys to prevent multiple creation attempts on the same aggregate.
   *
   * note that this cache does not stay current with any updates or deletes to these entities! this cache
   * is not intended for use with interleaving create/update/delete, but rather for a series of create calls.
   */
  protected var sessionCreations = Map[Unpersisted[E], Persisted[E]]()

  /** pull a create result out of the cache for the given unpersisted. if it's not there, then create it,
   * cache it, and return it */
  protected def getSessionCreationOrElse(unpersisted: Unpersisted[E], create: => Future[Persisted[E]])
  : Future[Persisted[E]] = {
    sessionCreations.get(unpersisted).map(Future(_)).getOrElse {
      create.map { persisted =>
        sessionCreations += (unpersisted -> persisted)
        persisted
      }
    }
  }

  // TODO pt 91219980 rewrite patchUnpersistedAssocs to use traversor
  /** returns a version of the aggregate where all unpersisted associations are persisted */
  protected def patchUnpersistedAssocs(entity: E): Future[E] = {
    var futureE = Future { entity }
    futureE = entityType.assocProps.foldLeft(futureE) { (futureE, prop) =>
      futureE flatMap { e => persistAssocWhenUnpersisted(e, prop) }
    }
    futureE = entityType.assocSetProps.foldLeft(futureE) { (futureE, prop) =>
      futureE flatMap { e => persistUnpersistedMembersOfAssocSet(e, prop) }
    }
    futureE = entityType.assocOptionProps.foldLeft(futureE) { (e, prop) =>
      futureE flatMap { e => persistUnpersistedMembersOfAssocOption(e, prop) }
    }
    futureE
  }

  private def persistAssocWhenUnpersisted[Associatee <: RootEntity](
    e: E,
    prop: EmblemProp[E, Assoc[Associatee]])
  : Future[E] =
    for (assoc <- persistAssocWhenUnpersisted(prop.get(e))) yield prop.set(e, assoc)

  private def persistAssocWhenUnpersisted[
    Associatee <: RootEntity](
    assoc: Assoc[Associatee])
  : Future[Assoc[Associatee]] = {
    assoc match {
      case UnpersistedAssoc(u) =>
        val repo = repoPool(assoc.associateeTypeKey)
        for (persisted <- repo.create(u)) yield persisted.id
      case _ => Future(assoc)
    }
  }

  private def persistUnpersistedMembersOfAssocSet[
    Associatee <: RootEntity](
    e: E,
    assocSetProp: EmblemProp[E, Set[Assoc[Associatee]]])
  : Future[E] = {
    val assocFutureSet = for (
      assoc <- assocSetProp.get(e)
    ) yield persistAssocWhenUnpersisted(assoc)

    for (
      assocSet <- Future.sequence(assocFutureSet)
    ) yield assocSetProp.set(e, assocSet)
  }

  private def persistUnpersistedMembersOfAssocOption[
    Associatee <: RootEntity](
    e: E,
    assocOptionProp: EmblemProp[E, Option[Assoc[Associatee]]])
  : Future[E] = {
    val assocFutureOption = for (
      unpersisted <- assocOptionProp.get(e)
    ) yield persistAssocWhenUnpersisted(unpersisted)

    val assocOptionFuture: Future[Option[Assoc[Associatee]]] = assocFutureOption match {
      case Some(f) => f map { assoc => Some(assoc) }
      case None => Future { None }
    }

    for (assocOption <- assocOptionFuture) yield assocOptionProp.set(e, assocOption)
  }
  
}
