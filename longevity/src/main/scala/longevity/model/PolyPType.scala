package longevity.model

/** the base type for a family of persistent types. mix this in to your
 * [[PType persistent type]] when it represents an abstract persistent
 * type with concrete subtypes.
 * 
 * @tparam M the domain model
 * @tparam P the persistent class
 */
trait PolyPType[M, P] extends PType[M, P] {

  override def toString = s"PolyPType[${pTypeKey.name}]"

}
