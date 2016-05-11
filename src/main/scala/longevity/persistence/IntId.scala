package longevity.persistence

import longevity.subdomain.persistent.Persistent

private case class IntId[P <: Persistent](i: Int) extends PersistedAssoc[P] {
  private[longevity] val _lock = 0
  def widen[Q >: P <: Persistent] = IntId[Q](i)
}
