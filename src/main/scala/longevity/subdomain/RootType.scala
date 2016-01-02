package longevity.subdomain

import emblem.basicTypes.isBasicType
import emblem.imports._
import longevity.exceptions.subdomain.root.EarlyIndexAccessException
import longevity.exceptions.subdomain.root.EarlyKeyAccessException
import longevity.exceptions.subdomain.root.LateIndexDefException
import longevity.exceptions.subdomain.root.LateKeyDefException
import longevity.subdomain.root._

/** a type class for a domain entity that serves as an aggregate root */
abstract class RootType[
  R <: Root](
  implicit private val rootTypeKey: TypeKey[R],
  implicit private val shorthandPool: ShorthandPool = ShorthandPool.empty)
extends EntityType[R] {

  private var registered = false

  private [subdomain] def register = {
    assert(!registered)
    registered = true
  }

  private var keyBuffer = Set[Key[R]]()
  private var indexBuffer = Set[Index[R]]()

  /** the keys for this root type. you populate this set by repeatedly calling either of the
   * `RootType.key` methods in your class initializer. you should only attempt to access this set
   * after your `RootType` is fully initialized.
   * @throws longevity.exceptions.subdomain.SubdomainException on attempt to access this set before the
   * `RootType` is fully initialized
   */
  lazy val keys: Set[Key[R]] = {
    if (!registered) throw new EarlyKeyAccessException
    keyBuffer
  }

  /** the indexes for this root type. you populate this set by repeatedly calling either of the
   * `RootType.index` methods in your class initializer. you should only attempt to access this set
   * after your `RootType` is fully initialized.
   * @throws longevity.exceptions.subdomain.SubdomainException on attempt to access this set before the
   * `RootType` is fully initialized
   */
  lazy val indexes: Set[Index[R]] = {
    if (!registered) throw new EarlyIndexAccessException
    indexBuffer
  }

  /** constructs a [[longevity.subdomain.root.Prop]] from a path
   * @throws longevity.exceptions.subdomain.root.PropException if any step along the path does not exist, or
   * any non-final step along the path is not an entity, or the final step along the path is not a
   * [[Shorthand]], an [[Assoc]] or a basic type
   * @see `emblem.basicTypes`
   */
  def prop[A : TypeKey](path: String): Prop[R, A] = Prop(path, emblem, entityTypeKey, shorthandPool)

  /** constructs a key for this root type based on the supplied set of property paths
   * @param propPathHead one of the property paths for the properties that define this key
   * @param propPathTail any remaining property paths for the properties that define this key
   * @throws longevity.exceptions.subdomain.root.PropException if any of the supplied property paths are
   * invalid
   * @throws longevity.exceptions.subdomain.SubdomainException on attempt to create a new key after the
   * `RootType` is fully initialized
   * @see Prop.apply
   */
  def key(propPathHead: String, propPathTail: String*): Key[R] = {
    if (registered) throw new LateKeyDefException
    val propPaths = propPathHead :: propPathTail.toList
    val key = Key(propPaths.map(Prop.unbounded(_, emblem, entityTypeKey, shorthandPool)))
    keyBuffer += key
    key
  }

  /** constructs a key for this root type based on the supplied set of key props
   * @param propsHead one of the properties that define this key
   * @param propsTail any remaining properties that define this key
   * @throws longevity.exceptions.subdomain.SubdomainException on attempt to create a new key after the
   * `RootType` is fully initialized
   */
  def key(propsHead: Prop[R, _], propsTail: Prop[R, _]*): Key[R] = {
    if (registered) throw new LateKeyDefException
    val key = Key(propsHead :: propsTail.toList)
    keyBuffer += key
    key
  }

  /** constructs an index for this root type based on the supplied set of property paths
   * @param propPathHead one of the property paths for the properties that define this index
   * @param propPathTail any remaining property paths for the properties that define this index
   * @throws longevity.exceptions.subdomain.root.PropException if any of the supplied property paths are
   * invalid
   * @throws longevity.exceptions.subdomain.SubdomainException on attempt to create a new index after the
   * `RootType` is fully initialized
   * @see Prop.apply
   */
  def index(propPathHead: String, propPathTail: String*): Index[R] = {
    if (registered) throw new LateIndexDefException
    val propPaths = propPathHead :: propPathTail.toList
    val index = Index(propPaths.map(Prop.unbounded(_, emblem, entityTypeKey, shorthandPool)))
    indexBuffer += index
    index
  }

  /** constructs a index for this root type based on the supplied set of index props
   * 
   * @param propsHead one of the properties that define this index
   * @param propsTail any remaining properties that define this index
   * @throws longevity.exceptions.subdomain.SubdomainException on attempt to create a new index after the
   * `RootType` is fully initialized
   */
  def index(propsHead: Prop[R, _], propsTail: Prop[R, _]*): Index[R] = {
    if (registered) throw new LateIndexDefException
    val index = Index(propsHead :: propsTail.toList)
    indexBuffer += index
    index
  }

  /** contains implicit imports to make the query DSL work */
  lazy val queryDsl = new QueryDsl[R]

  /** translates the query into a validated query by resolving all the property paths to properties.
   * throws exception if the property value supplied does not match the property type.
   * 
   * @throws longevity.exceptions.subdomain.root.PropValTypeException if a dynamic part of the query is mistyped
   */
  def validateQuery(query: Query[R]): ValidatedQuery[R] = {
    query match {
      case q: ValidatedQuery[R] => q
      case q: EqualityQuery[R, _] =>
        def static[A : TypeKey](qq: EqualityQuery[R, A]) = {
          val prop = Prop[R, A](qq.path, emblem, entityTypeKey, shorthandPool)
          VEqualityQuery[R, A](prop, qq.op, qq.value)
        }
        static(q)(q.valTypeKey)
      case q: OrderingQuery[R, _] =>
        def static[A : TypeKey](qq: OrderingQuery[R, A]) = {
          val prop = Prop[R, A](qq.path, emblem, entityTypeKey, shorthandPool)
          VOrderingQuery[R, A](prop, qq.op, qq.value)
        }
        static(q)(q.valTypeKey)
      case q: ConditionalQuery[R] =>
        VConditionalQuery(
          validateQuery(q.lhs),
          q.op,
          validateQuery(q.rhs))
    }
  }
  
}
