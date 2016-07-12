package longevity.persistence

import longevity.subdomain.persistent.Persistent

/** a database identifier */
private[longevity] trait DatabaseId[P <: Persistent] {

  /** returns a copy of this database identifier with a wider type bound */
  def widen[Q >: P <: Persistent]: DatabaseId[Q]

}
