package longevity.subdomain

/** an [[Assoc]] to a root that has not been persisted. for use with
 * [[longevity.persistence.RepoPool.createMany]]
 */
private[longevity] case class UnpersistedAssoc[R <: Root](unpersisted: R) extends Assoc[R] {
  private[longevity] val _lock = 0
  def isPersisted = false
}
