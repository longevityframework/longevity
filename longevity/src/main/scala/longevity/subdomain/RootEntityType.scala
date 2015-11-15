package longevity.subdomain

import emblem.basicTypes.isBasicType
import emblem.imports._
import longevity.exceptions.subdomain.SubdomainException

/** a type class for a domain entity that serves as an aggregate root */
abstract class RootEntityType[
  E <: RootEntity](
  implicit private val rootTypeKey: TypeKey[E],
  implicit private val shorthandPool: ShorthandPool = ShorthandPool.empty)
extends EntityType[E] {

  private var registered = false

  private [subdomain] def register = {
    assert(!registered)
    registered = true
  }

  private var natKeyBuffer = Set[NatKey[E]]()

  /** the natural keys for this root entity type. you populate this set by repeatedly calling either of the
   * `RootEntityType.natKey` methods in your class initializer. you should only attempt to access this set
   * after your `RootEntityType` is fully initialized.
   * @throws longevity.exceptions.SubdomainException on attempt to access this set before the `RootEntityType`
   * is fully initialized
   */
  lazy val natKeys: Set[NatKey[E]] = {
    if (!registered) throw new SubdomainException(
      s"cannot access RootEntityType.natKeys for $this until after the subdomain has been initialized")
    natKeyBuffer
  }

  /** constructs a [[NatKeyProp]] from a path
   * @throws longevity.exceptions.InvalidNatKeyPropPathException if any step along the path does not exist, or
   * any non-final step along the path is not an entity, or the final step along the path is not an [[Assoc]] or
   * a basic type
   * @see `emblem.basicTypes`
   */
  def natKeyProp(path: String): NatKeyProp[E] = NatKeyProp(path, emblem, entityTypeKey, shorthandPool)

  /** constructs a natural key for this root entity type based on the supplied set of property paths.
   * @param propPathHead one of the property paths for the properties that define this nat key
   * @param propPathTail any remaining property paths for the properties that define this nat key
   * @throws longevity.exceptions.InvalidNatKeyPropPathException if any of the supplied property paths are
   * invalid
   * @throws longevity.exceptions.SubdomainException on attempt to create a new nat key after the
   * `RootEntityType` is fully initialized
   * @see NatKeyProp.apply
   */
  def natKey(propPathHead: String, propPathTail: String*): NatKey[E] = {
    if (registered)
      throw new SubdomainException("cannot create new natural keys after the subdomain has been initialized")
    val propPaths = propPathTail.toSet + propPathHead
    val key = NatKey(propPaths.map(natKeyProp(_)))
    natKeyBuffer += key
    key
  }

  /** constructs a natural key for this root entity type based on the supplied set of nat key props.
   * @param propsHead one of the properties that define this nat key
   * @param propsTail any remaining properties that define this nat key
   * @throws longevity.exceptions.SubdomainException on attempt to create a new nat key after the `RootEntityType`
   * is fully initialized
   */
  def natKey(propsHead: NatKeyProp[E], propsTail: NatKeyProp[E]*): NatKey[E] = {
    if (registered)
      throw new SubdomainException("cannot create new natural keys after the subdomain has been initialized")
    val key = NatKey(propsTail.toSet + propsHead)
    natKeyBuffer += key
    key
  }

}
