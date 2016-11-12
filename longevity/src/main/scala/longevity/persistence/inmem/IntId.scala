package longevity.persistence.inmem

import longevity.persistence.DatabaseId

private case class IntId[P](i: Int) extends DatabaseId[P] {
  private[longevity] val _lock = 0
  def widen[Q >: P] = IntId[Q](i)
}
