package longevity.persistence

import longevity.subdomain.Assoc
import longevity.subdomain.Persistent

/** the result of deleting a persistent entity
 * @param p the persistent entity
 * @param assoc an association to the deleted entity
 */
case class Deleted[P <: Persistent] private[persistence] (
  val p: P,
  val assoc: Assoc[P])
