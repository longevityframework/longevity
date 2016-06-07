package longevity.persistence

import longevity.subdomain.Assoc
import longevity.subdomain.persistent.Persistent

/** the result of deleting a persistent object
 * 
 * @param p the persistent object
 * @param assoc an association to the deleted object
 */
case class Deleted[P <: Persistent] private[persistence] (
  val p: P,
  val assoc: Assoc[P]) {

  /** returns the persistent object that was deleted */
  def get: P = p

}
