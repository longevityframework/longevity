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

  private var keyBuffer = Set[Key[E]]()

  /** the natural keys for this root entity type. you populate this set by repeatedly calling either of the
   * `RootEntityType.key` methods in your class initializer. you should only attempt to access this set
   * after your `RootEntityType` is fully initialized.
   * @throws longevity.exceptions.subdomain.SubdomainException on attempt to access this set before the
   * `RootEntityType` is fully initialized
   */
  lazy val keys: Set[Key[E]] = {
    if (!registered) throw new SubdomainException(
      s"cannot access RootEntityType.keys for $this until after the subdomain has been initialized")
    keyBuffer
  }

  /** constructs a [[KeyProp]] from a path
   * @throws longevity.exceptions.subdomain.InvalidKeyPropPathException if any step along the path does not
   * exist, or any non-final step along the path is not an entity, or the final step along the path is not an
   * [[Assoc]] or a basic type
   * @see `emblem.basicTypes`
   */
  def keyProp(path: String): KeyProp[E] = KeyProp(path, emblem, entityTypeKey, shorthandPool)

  /** constructs a natural key for this root entity type based on the supplied set of property paths.
   * @param propPathHead one of the property paths for the properties that define this nat key
   * @param propPathTail any remaining property paths for the properties that define this nat key
   * @throws longevity.exceptions.subdomain.InvalidKeyPropPathException if any of the supplied property paths are
   * invalid
   * @throws longevity.exceptions.subdomain.SubdomainException on attempt to create a new nat key after the
   * `RootEntityType` is fully initialized
   * @see KeyProp.apply
   */
  def key(propPathHead: String, propPathTail: String*): Key[E] = {
    if (registered)
      throw new SubdomainException("cannot create new natural keys after the subdomain has been initialized")
    val propPaths = propPathTail.toSet + propPathHead
    val key = Key(propPaths.map(keyProp(_)))
    keyBuffer += key
    key
  }

  /** constructs a natural key for this root entity type based on the supplied set of nat key props.
   * @param propsHead one of the properties that define this nat key
   * @param propsTail any remaining properties that define this nat key
   * @throws longevity.exceptions.subdomain.SubdomainException on attempt to create a new nat key after the
   * `RootEntityType` is fully initialized
   */
  def key(propsHead: KeyProp[E], propsTail: KeyProp[E]*): Key[E] = {
    if (registered)
      throw new SubdomainException("cannot create new natural keys after the subdomain has been initialized")
    val key = Key(propsTail.toSet + propsHead)
    keyBuffer += key
    key
  }

}
