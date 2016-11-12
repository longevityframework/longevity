package longevity.subdomain.ptype


/** an index for a persistent type
 *
 * @tparam P the persistent type
 * @param props the sequence of properties that make up this index
 */
case class Index[P] private [subdomain] (val props: Seq[Prop[P, _]])
