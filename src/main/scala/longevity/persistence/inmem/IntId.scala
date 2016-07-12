package longevity.persistence.inmem

import longevity.persistence.DatabaseId
import longevity.subdomain.persistent.Persistent

private case class IntId[P <: Persistent](i: Int) extends DatabaseId[P] {
  private[longevity] val _lock = 0
  def widen[Q >: P <: Persistent] = IntId[Q](i)
}
