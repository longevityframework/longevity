package longevity.subdomain.ptype

import longevity.subdomain.ShorthandPool
import longevity.subdomain.persistent.Persistent

/** an index for this persistent type
 * @param props the sequence of properties that make up this index
 */
case class Index[P <: Persistent] private [subdomain] (
  val props: Seq[Prop[P, _]])(
  private implicit val shorthandPool: ShorthandPool) {
}
