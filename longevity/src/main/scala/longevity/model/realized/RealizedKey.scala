package longevity.model.realized

import longevity.model.KVEv
import longevity.model.ptype.Key

private[longevity] class RealizedKey[M, P, V] private [realized](
  val key: Key[M, P],
  val realizedProp: RealizedProp[P, V],
  private[longevity] val ev: KVEv[M, P, V]) {

  val keyValTypeKey = realizedProp.propTypeKey

  /** returns the [[KeyVal]] for the supplied persistent
   * @param p the persistent
   */
  def keyValForP(p: P): V = realizedProp.propVal(p)

  /** returns a copy of the persistent with an updated key value */
  def updateKeyVal(p: P, keyVal: V): P = {
    realizedProp.updatePropVal(p, keyVal)
  }

  override def toString = s"Realized$key"

  override def hashCode = key.hashCode

  override def equals(that: Any) =
    that.isInstanceOf[RealizedKey[M, P, V]] && key == that.asInstanceOf[RealizedKey[M, P, V]].key

}
