package longevity.persistence

import longevity.subdomain.Assoc
import longevity.subdomain.Root

/** the result of deleting an aggregate
 * @param root the aggregate root
 * @param assoc an association to the deleted aggregate
 */
case class Deleted[R <: Root] private[persistence] (
  val root: R,
  val assoc: Assoc[R])
