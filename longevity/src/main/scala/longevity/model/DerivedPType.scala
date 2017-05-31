package longevity.model

import emblem.TypeKey
import scala.reflect.runtime.universe.TypeTag

/** one of the derived types in a family of persistent classes. mix this in to
 * your [[PType persistent type]] when it represents a concrete subtype of a
 * [[PolyPType]].
 *
 * @tparam M the domain model
 * @tparam P the persistent class
 * @tparam Poly the parent persistent class
 */
abstract class DerivedPType[M : ModelEv, P : TypeTag, Poly >: P : TypeTag] extends PType[M, P] {

  private[longevity] val polyPTypeKey: TypeKey[Poly] = implicitly[TypeKey[Poly]]

  override def toString = s"DerivedPType[${pTypeKey.name}, ${polyPTypeKey.name}]"

}
