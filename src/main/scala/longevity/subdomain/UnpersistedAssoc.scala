package longevity.subdomain

import longevity.subdomain.persistent.Persistent

/** an [[Assoc]] to a persistent that has not yet been persisted. for use with
 * [[longevity.persistence.RepoPool.createMany]]
 *
 * @param unpersisted the unpersisted associatee
 */
private[longevity] case class UnpersistedAssoc[P <: Persistent](unpersisted: P)
extends Assoc[P] {

  private[longevity] val _lock = 0

  def isPersisted = false

}
