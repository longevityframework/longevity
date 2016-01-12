package longevity.persistence

import longevity.subdomain.Assoc
import longevity.subdomain.Root
import longevity.subdomain.root.KeyVal
import longevity.subdomain.root.Query
import scala.concurrent.Future

/** a repository for aggregate roots of type `R` */
trait Repo[R <: Root] {

  /** creates the aggregate */
  def create(unpersisted: R): Future[PState[R]]

  /** retrieves an optional persisted assoc
   * @throws longevity.exceptions.subdomain.AssocIsUnpersistedException whenever the assoc is not persisted
   */
  def retrieve(assoc: Assoc[R]): Future[Option[PState[R]]]

  /** retrieves a non-optional persisted assoc
   * 
   * throws NoSuchElementException whenever the assoc does not refer to an aggregate in the repository.
   * most likely it was deleted.
   * 
   * @throws longevity.exceptions.subdomain.AssocIsUnpersistedException whenever the assoc is not persisted
   */
  def retrieveOne(assoc: Assoc[R]): Future[PState[R]]

  /** retrieves an optional aggregate by a key value */
  def retrieve(keyVal: KeyVal[R]): Future[Option[PState[R]]]

  /** retrieves a non-optional aggregate by a key value
   * 
   * throws NoSuchElementException whenever the key value does not refer to an aggregate in the repository
   */
  def retrieveOne(keyVal: KeyVal[R]): Future[PState[R]]

  /** retrieves the aggregate by a query */
  def retrieveByQuery(query: Query[R]): Future[Seq[PState[R]]]

  /** updates the aggregate */
  def update(state: PState[R]): Future[PState[R]]

  /** deletes the aggregate */
  def delete(state: PState[R]): Future[Deleted[R]]

}
