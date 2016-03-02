package longevity.persistence

import longevity.subdomain.PRef
import longevity.subdomain.Root
import longevity.subdomain.root.Query
import scala.concurrent.Future

/** a repository for aggregate roots of type `R` */
trait Repo[R <: Root] {

  /** creates the aggregate */
  def create(unpersisted: R): Future[PState[R]]

  /** retrieves an optional aggregate from a persistent ref
   * 
   * @throws longevity.exceptions.subdomain.AssocIsUnpersistedException whenever
   * the persistent ref is an unpersisted assoc
   */
  def retrieve(ref: PRef[R]): Future[Option[PState[R]]]

  /** retrieves a non-optional aggregate from a persistent ref
   * 
   * throws NoSuchElementException whenever the persistent ref does not refer
   * to an aggregate in the repository
   * 
   * @throws longevity.exceptions.subdomain.AssocIsUnpersistedException whenever
   * the persistent ref is an unpersisted assoc
   */
  def retrieveOne(ref: PRef[R]): Future[PState[R]]

  /** retrieves the aggregate by a query */
  def retrieveByQuery(query: Query[R]): Future[Seq[PState[R]]]

  /** updates the aggregate */
  def update(state: PState[R]): Future[PState[R]]

  /** deletes the aggregate */
  def delete(state: PState[R]): Future[Deleted[R]]

}
