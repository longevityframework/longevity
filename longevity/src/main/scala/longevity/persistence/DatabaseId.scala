package longevity.persistence


/** a database identifier */
private[longevity] trait DatabaseId[P] {

  /** returns a copy of this database identifier with a wider type bound */
  def widen[Q >: P]: DatabaseId[Q]

}
