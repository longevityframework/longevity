package longevity.subdomain.realized

import emblem.TypeKey
import emblem.emblematic.Emblematic
import emblem.typeKey
import longevity.subdomain.KeyVal
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Key

/** TODO */
private[longevity] case class RealizedKey[P <: Persistent, V <: KeyVal[P] : TypeKey] private [subdomain](
  key: Key[P, V])(
  val realizedProp: RealizedProp[P, V],
  emblematic: Emblematic) {

  val keyValTypeKey = typeKey[V]

  /** returns the [[KeyVal]] for the supplied persistent
   * @param p the persistent
   */
  def keyValForP(p: P): V = realizedProp.propVal(p)

  // oh my scala typer
  def keyValForP_TODO(p: P): KeyVal[P] = keyValForP(p)

  def updateKeyVal(p: P, keyVal: V): P = {
    realizedProp.updatePropVal(p, keyVal)
  }

  override def toString = s"Realized$key"
}
