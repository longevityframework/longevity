package longevity.subdomain.root

import longevity.subdomain._

/** an index for this root entity type
 * @param props the sequence of properties that make up this index
 */
case class Index[E <: RootEntity] private [subdomain] (
  val props: Seq[Prop[E, _]])(
  private implicit val shorthandPool: ShorthandPool) {
  private lazy val propPathToProp: Map[String, Prop[E, _]] = props.map(p => p.path -> p).toMap
}
