package longevity.persistence.inmem

import longevity.persistence.DatabaseId

private case class IntId(i: Int) extends DatabaseId {
  private[longevity] val _lock = 0
}
