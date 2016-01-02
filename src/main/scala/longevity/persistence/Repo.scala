package longevity.persistence

import longevity.subdomain.Root
import longevity.subdomain.root.KeyVal
import longevity.subdomain.root.Query
import scala.concurrent.Future

/** a repository for aggregate roots of type `R` */
trait Repo[R <: Root] {

  /** creates the aggregate */
  def create(unpersisted: R): Future[PState[R]]

  /** retrieves the aggregate by a key value */
  def retrieve(keyVal: KeyVal[R]): Future[Option[PState[R]]]

  /** retrieves the aggregate by a query */
  def retrieveByQuery(query: Query[R]): Future[Seq[PState[R]]]

  /** updates the aggregate */
  def update(state: PState[R]): Future[PState[R]]

  /** deletes the aggregate */
  def delete(state: PState[R]): Future[Deleted[R]]

}
