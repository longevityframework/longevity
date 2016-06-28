package longevity.persistence

import longevity.subdomain.persistent.Persistent

// TODO rename to DatabaseId
// TODO rewrite scaladocs

/** an [[longevity.subdomain.Assoc Assoc]] to a persistent object that has been
 * persisted
 */
private[longevity] trait PersistedAssoc[P <: Persistent] {

  /** returns a copy of this persisted assoc with a wider type bound */
  def widen[Q >: P <: Persistent]: PersistedAssoc[Q]

}
