package longevity.subdomain

import emblem.basicTypes.isBasicType
import emblem.imports._

/** a type class for a domain entity that serves as an aggregate root */
abstract class RootEntityType[
  E <: RootEntity](
  implicit private val rootTypeKey: TypeKey[E], // TODO: cant i use entityTypeKey??
  implicit private val shorthandPool: ShorthandPool)
extends EntityType[E] {

  /** constructs a [[NatKeyProp]] from a path
   * @throws InvalidNatKeyPropPathException if any step along the path does not exist
   * @throws InvalidNatKeyPropPathException if any non-final step along the path is not an entity
   * @throws InvalidNatKeyPropPathException if the final step along the path is not an [[Assoc]] or a basic type
   * @see `emblem.basicTypes`
   */
  def natKeyProp(path: String): NatKeyProp[E] = NatKeyProp(path, emblem, entityTypeKey, shorthandPool)

  /** constructs a natural key for this root entity type based on the supplied set of property paths.
   * @param propPathHead one of the property paths for the properties that define this nat key
   * @param propPathTail any remaining property paths for the properties that define this nat key
   * @throws InvalidNatKeyPropPathException if any of the supplied property paths are invalid
   * @see NatKeyProp.apply
   */
  def natKey(propPathHead: String, propPathTail: String*): NatKey[E] = {
    val propPaths = propPathTail.toSet + propPathHead
    NatKey(propPaths.map(natKeyProp(_)))
  }

  /** constructs a natural key for this root entity type based on the supplied set of nat key props.
   * @param propsHead one of the properties that define this nat key
   * @param propsTail any remaining properties that define this nat key
   */
  def natKey(propsHead: NatKeyProp[E], propsTail: NatKeyProp[E]*): NatKey[E] = {
    NatKey(propsTail.toSet + propsHead)
  }

}
