package longevity.persistence

import longevity.subdomain.PRef
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Query
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** a repository for persistent entities of type `P` */
trait Repo[P <: Persistent] {

  /** creates the persistent entity
   * 
   * @param unpersisted the persistent entity to create
   * @param executionContext the execution context
   */
  def create(unpersisted: P)(implicit executionContext: ExecutionContext): Future[PState[P]]

  /** retrieves an optional entity from a persistent ref
   * 
   * @param ref the reference to use to look up the entity. this could be a
   * [[longevity.subdomain.ptype.KeyVal KeyVal]] or an
   * [[longevity.subdomain.Assoc Assoc]]
   * 
   * @param executionContext the execution context
   * 
   * @throws longevity.exceptions.persistence.AssocIsUnpersistedException
   * whenever the persistent ref is an unpersisted assoc
   */
  def retrieve(ref: PRef[P])(implicit executionContext: ExecutionContext): Future[Option[PState[P]]]

  /** retrieves a non-optional entity from a persistent ref
   * 
   * throws NoSuchElementException whenever the persistent ref does not refer
   * to an entity in the repository
   * 
   * @param ref the reference to use to look up the entity. this could be a
   * [[longevity.subdomain.ptype.KeyVal KeyVal]] or an
   * [[longevity.subdomain.Assoc Assoc]]
   *
   * @param executionContext the execution context
   * 
   * @throws longevity.exceptions.persistence.AssocIsUnpersistedException
   * whenever the persistent ref is an unpersisted assoc
   */
  def retrieveOne(ref: PRef[P])(implicit executionContext: ExecutionContext): Future[PState[P]]

  /** retrieves multiple persistent entities by a query
   * 
   * @param query the query to execute
   * @param executionContext the execution context
   */
  def retrieveByQuery(query: Query[P])(implicit executionContext: ExecutionContext)
  : Future[Seq[PState[P]]]

  /** updates the persistent entity
   * 
   * @param state the persistent state of the entity to update
   * @param executionContext the execution context
   */
  def update(state: PState[P])(implicit executionContext: ExecutionContext): Future[PState[P]]

  /** deletes the persistent entity
   * 
   * @param state the persistent state of the entity to delete
   * @param executionContext the execution context
   */
  def delete(state: PState[P])(implicit executionContext: ExecutionContext): Future[Deleted[P]]

}
