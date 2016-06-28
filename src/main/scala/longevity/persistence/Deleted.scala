package longevity.persistence

import longevity.subdomain.persistent.Persistent

/** the result of deleting a persistent object
 * 
 * @param p the persistent object
 * @param assoc an association to the deleted object
 */
case class Deleted[P <: Persistent] private[persistence] (
  private[persistence] val p: P,
  private[persistence] val assoc: PersistedAssoc[P]) {

  /** returns the persistent object that was deleted */
  def get: P = p

  /** returns a copy of this deleted with a wider type bound */
  def widen[Q >: P <: Persistent]: Deleted[Q] = new Deleted[Q](p, assoc.widen[Q])

}
