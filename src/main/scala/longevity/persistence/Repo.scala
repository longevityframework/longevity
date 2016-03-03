package longevity.persistence

import longevity.subdomain.PRef
import longevity.subdomain.Root
import longevity.subdomain.root.Query
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** a repository for aggregate roots of type `R` */
trait Repo[R <: Root] {

  /** creates the aggregate
   * 
   * @param unpersisted the root of the aggregate to create
   * @param executionContext the execution context
   */
  def create(unpersisted: R)(implicit executionContext: ExecutionContext): Future[PState[R]]

  /** retrieves an optional aggregate from a persistent ref
   * 
   * @param ref the reference to use to look up the aggregate. this could be a
   * [[longevity.subdomain.root.KeyVal KeyVal]] or an
   * [[longevity.subdomain.Assoc Assoc]]
   * 
   * @param executionContext the execution context
   * 
   * @throws longevity.exceptions.persistence.AssocIsUnpersistedException
   * whenever the persistent ref is an unpersisted assoc
   */
  def retrieve(ref: PRef[R])(implicit executionContext: ExecutionContext): Future[Option[PState[R]]]

  /** retrieves a non-optional aggregate from a persistent ref
   * 
   * throws NoSuchElementException whenever the persistent ref does not refer
   * to an aggregate in the repository
   * 
   * @param ref the reference to use to look up the aggregate. this could be a
   * [[longevity.subdomain.root.KeyVal KeyVal]] or an
   * [[longevity.subdomain.Assoc Assoc]]
   *
   * @param executionContext the execution context
   * 
   * @throws longevity.exceptions.persistence.AssocIsUnpersistedException
   * whenever the persistent ref is an unpersisted assoc
   */
  def retrieveOne(ref: PRef[R])(implicit executionContext: ExecutionContext): Future[PState[R]]

  /** retrieves multiple aggregates by a query
   * 
   * @param query the query to execute
   * @param executionContext the execution context
   */
  def retrieveByQuery(query: Query[R])(implicit executionContext: ExecutionContext)
  : Future[Seq[PState[R]]]

  /** updates the aggregate
   * 
   * @param state the persistent state of the aggregate to update
   * @param executionContext the execution context
   */
  def update(state: PState[R])(implicit executionContext: ExecutionContext): Future[PState[R]]

  /** deletes the aggregate
   * 
   * @param state the persistent state of the aggregate to delete
   * @param executionContext the execution context
   */
  def delete(state: PState[R])(implicit executionContext: ExecutionContext): Future[Deleted[R]]

}
