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

  // TODO we will probably have to constraint props in a similar way
  private var keyBuffer = Set[Key[E]]()
  private var indexBuffer = Set[Index[E]]()

  /** the keys for this root entity type. you populate this set by repeatedly calling either of the
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

  /** the indexes for this root entity type. you populate this set by repeatedly calling either of the
   * `RootEntityType.index` methods in your class initializer. you should only attempt to access this set
   * after your `RootEntityType` is fully initialized.
   * @throws longevity.exceptions.SubdomainException on attempt to access this set before the `RootEntityType`
   * is fully initialized
   */
  lazy val indexes: Set[Index[E]] = {
    if (!registered) throw new SubdomainException(
      s"cannot access RootEntityType.indexes for $this until after the subdomain has been initialized")
    indexBuffer
  }

  /** constructs a [[Prop]] from a path
   * @throws longevity.exceptions.InvalidPropPathException if any step along the path does not exist, or
   * any non-final step along the path is not an entity, or the final step along the path is not an [[Assoc]] or
   * a basic type
   * @see `emblem.basicTypes`
   */
  def prop[A : TypeKey](path: String): Prop[E, A] = Prop(path, emblem, entityTypeKey, shorthandPool)

  /** constructs a key for this root entity type based on the supplied set of property paths
   * @param propPathHead one of the property paths for the properties that define this key
   * @param propPathTail any remaining property paths for the properties that define this key
   * @throws longevity.exceptions.subdomain.InvalidKeyPropPathException if any of the supplied property paths are
   * invalid
   * @throws longevity.exceptions.subdomain.SubdomainException on attempt to create a new nat key after the
   * `RootEntityType` is fully initialized
   * @see Prop.apply
   */
  def key(propPathHead: String, propPathTail: String*): Key[E] = {
    if (registered)
      throw new SubdomainException("cannot create new keys after the subdomain has been initialized")
    val propPaths = propPathHead :: propPathTail.toList
    val key = Key(propPaths.map(Prop.unbounded(_, emblem, entityTypeKey, shorthandPool)))
    keyBuffer += key
    key
  }

  /** constructs a key for this root entity type based on the supplied set of key props
   * @param propsHead one of the properties that define this key
   * @param propsTail any remaining properties that define this key
   * @throws longevity.exceptions.subdomain.SubdomainException on attempt to create a new key after the
   * `RootEntityType` is fully initialized
   */
  def key(propsHead: Prop[E, _], propsTail: Prop[E, _]*): Key[E] = {
    if (registered)
      throw new SubdomainException("cannot create new keys after the subdomain has been initialized")
    val key = Key(propsHead :: propsTail.toList)
    keyBuffer += key
    key
  }

  /** constructs an index for this root entity type based on the supplied set of property paths
   * @param propPathHead one of the property paths for the properties that define this index
   * @param propPathTail any remaining property paths for the properties that define this index
   * @throws longevity.exceptions.InvalidPropPathException if any of the supplied property paths are
   * invalid
   * @throws longevity.exceptions.SubdomainException on attempt to create a new index after the
   * `RootEntityType` is fully initialized
   * @see Prop.apply
   */
  def index(propPathHead: String, propPathTail: String*): Index[E] = {
    if (registered)
      throw new SubdomainException("cannot create new indexes after the subdomain has been initialized")
    val propPaths = propPathHead :: propPathTail.toList
    val index = Index(propPaths.map(prop(_)))
    indexBuffer += index
    index
  }

  /** constructs a index for this root entity type based on the supplied set of index props
   * @param propsHead one of the properties that define this index
   * @param propsTail any remaining properties that define this index
   * @throws longevity.exceptions.SubdomainException on attempt to create a new index after the `RootEntityType`
   * is fully initialized
   */
  def index(propsHead: Prop[E, _], propsTail: Prop[E, _]*): Index[E] = {
    if (registered)
      throw new SubdomainException("cannot create new indexes after the subdomain has been initialized")
    val index = Index(propsHead :: propsTail.toList)
    indexBuffer += index
    index
  }

  /** validates the query. throws exception if not valid. translates DRelationalQuery into SRelationalQuery
   * @throws longevity.exceptions.subdomain.PropTypeMismatchException if a dynamic part of the query is mistyped
   */
  def validateQuery(query: Query[E]): Query[E] = {
    query match {
      case q: SRelationalQuery[E, _] =>
        q
      case q: DRelationalQuery[E, _] =>
        def static[A : TypeKey](qq: DRelationalQuery[E, A]) = {
          val prop = Prop[E, A](qq.path, emblem, entityTypeKey, shorthandPool)
          SRelationalQuery[E, A](prop, qq.op, qq.value)
        }
        static(q)(q.valTypeKey)
      case q: ConditionalQuery[E] =>
        ConditionalQuery(
          validateQuery(q.lhs),
          q.op,
          validateQuery(q.rhs))
    }
  }
  
}
