package longevity.model

import emblem.TypeKey

/** one of the derived types in a family of persistent types. mix this in to
 * youe [[PType persistent type]] when it represents a concrete subtype of a
 * [[PolyPType]].
 */
abstract class DerivedPType[P : TypeKey, Poly >: P : TypeKey] extends PType[P] {

  private[longevity] val polyPTypeKey: TypeKey[Poly] = implicitly[TypeKey[Poly]]

  override def toString = s"DerivedPType[${pTypeKey.name}, ${polyPTypeKey.name}]"

}
