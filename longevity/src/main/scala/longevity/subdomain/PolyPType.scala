package longevity.subdomain

/** the base type for a family of persistent types. mix this in to your
 * [[PType persistent type]] when it represents an abstract persistent
 * type with concrete subtypes.
 */
trait PolyPType[P] extends PType[P] {

  override def toString = s"PolyPType[${pTypeKey.name}]"

}
