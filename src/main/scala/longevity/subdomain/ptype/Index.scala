package longevity.subdomain.ptype

import longevity.subdomain.persistent.Persistent

/** an index for this persistent type
 * 
 * @param props the sequence of properties that make up this index
 */
case class Index[P <: Persistent] private [subdomain] (val props: Seq[Prop[P, _]])
