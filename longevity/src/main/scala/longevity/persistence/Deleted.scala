package longevity.persistence


/** the result of deleting a persistent object
 * 
 * @param p the persistent object
 */
case class Deleted[P] private[persistence] (
  private[persistence] val p: P) {

  /** returns the persistent object that was deleted */
  def get: P = p

  /** returns a copy of this deleted with a wider type bound */
  def widen[Q >: P]: Deleted[Q] = new Deleted[Q](p)

}
