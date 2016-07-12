package longevity.persistence

import akka.NotUsed
import akka.stream.scaladsl.Source
import longevity.subdomain.KeyVal
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Query
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** a repository for persistent objects of type `P` */
trait Repo[P <: Persistent] {

  /** creates the persistent object
   * 
   * @param unpersisted the persistent object to create
   * @param executionContext the execution context
   */
  def create(unpersisted: P)(implicit executionContext: ExecutionContext): Future[PState[P]]

  /** retrieves an optional persistent object from a
   * [[longevity.subdomain.KeyVal key value]]
   *
   * @tparam V the type of the key value
   * @param keyVal the key value to use to look up the persistent object
   * @param executionContext the execution context
   */
  def retrieve[V <: KeyVal[P, V]](keyVal: V)(implicit executionContext: ExecutionContext)
  : Future[Option[PState[P]]]

  /** retrieves an optional persistent object from a
   * [[longevity.subdomain.KeyVal key value]]
   * 
   * throws NoSuchElementException whenever the persistent ref does not refer
   * to a persistent object in the repository
   * 
   * @tparam V the type of the key value
   * @param keyVal the key value to use to look up the persistent object
   * @param executionContext the execution context
   */
  def retrieveOne[V <: KeyVal[P, V]](keyVal: V)(implicit executionContext: ExecutionContext): Future[PState[P]]

  /** retrieves multiple persistent objects matching a query
   * 
   * @param query the query to execute
   * @param executionContext the execution context
   */
  def retrieveByQuery(query: Query[P])(implicit executionContext: ExecutionContext)
  : Future[Seq[PState[P]]]

  /** streams persistent objects matching a query
   * 
   * @param query the query to execute
   */
  def streamByQuery(query: Query[P]): Source[PState[P], NotUsed]

  /** updates the persistent object
   * 
   * @param state the persistent state of the persistent object to update
   * @param executionContext the execution context
   */
  def update(state: PState[P])(implicit executionContext: ExecutionContext): Future[PState[P]]

  /** deletes the persistent object
   * 
   * @param state the persistent state of the persistent object to delete
   * @param executionContext the execution context
   */
  def delete(state: PState[P])(implicit executionContext: ExecutionContext): Future[Deleted[P]]

}
