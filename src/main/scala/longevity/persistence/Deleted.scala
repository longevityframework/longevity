package longevity.persistence

import longevity.subdomain.Root

/** a deleted aggregate */
case class Deleted[R <: Root] private[persistence] (private val root: R) {

  /** returns the aggregate */
  def get: R = root

}
