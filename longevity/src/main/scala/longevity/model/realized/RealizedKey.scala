package longevity.model.realized

import emblem.TypeKey
import longevity.model.KeyVal
import longevity.model.ptype.Key

private[longevity] class RealizedKey[P, V <: KeyVal[P] : TypeKey] private [realized](
  val key: Key[P],
  val realizedProp: RealizedProp[P, V]) {

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
    that.isInstanceOf[RealizedKey[_, _]] && key == that.asInstanceOf[RealizedKey[_, _]].key

}
