package longevity.model

import emblem.TypeKey

/** one of the derived types in a family of persistent classes. mix this in to
 * youe [[PType persistent type]] when it represents a concrete subtype of a
 * [[PolyPType]].
 *
 * @tparam M the domain model
 * @tparam P the persistent class
 * @tparam Poly the parent persistent class
 */
abstract class DerivedPType[M : ModelEv, P : TypeKey, Poly >: P : TypeKey] extends PType[M, P] {

  private[longevity] val polyPTypeKey: TypeKey[Poly] = implicitly[TypeKey[Poly]]

  override def toString = s"DerivedPType[${pTypeKey.name}, ${polyPTypeKey.name}]"

}
