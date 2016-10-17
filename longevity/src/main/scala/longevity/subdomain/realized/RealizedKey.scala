package longevity.subdomain.realized

import emblem.TypeKey
import emblem.typeKey
import longevity.subdomain.KeyVal
import longevity.subdomain.Persistent
import longevity.subdomain.ptype.Key

private[longevity] class RealizedKey[
  P <: Persistent,
  V <: KeyVal[P, V] : TypeKey] private [subdomain](
  val key: Key[P, V],
  val realizedProp: RealizedProp[P, V]) {

  val keyValTypeKey = typeKey[V]

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
