package longevity.subdomain.root

import longevity.subdomain._

/** an index for this root type
 * @param props the sequence of properties that make up this index
 */
case class Index[R <: Root] private [subdomain] (
  val props: Seq[Prop[R, _]])(
  private implicit val shorthandPool: ShorthandPool) {
}
