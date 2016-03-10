package longevity.subdomain

import emblem.basicTypes.isBasicType
import emblem.imports._
import longevity.exceptions.subdomain.root.EarlyIndexAccessException
import longevity.exceptions.subdomain.root.EarlyKeyAccessException
import longevity.exceptions.subdomain.root.LateIndexDefException
import longevity.exceptions.subdomain.root.LateKeyDefException
import longevity.subdomain.root._

/** a type class for a domain entity that is stored in a persistent collection */
abstract class PType[
  P <: Persistent](
  implicit private val pTypeKey: TypeKey[P],
  implicit private val shorthandPool: ShorthandPool = ShorthandPool.empty)
extends EntityType[P] {

  private var registered = false

  private [subdomain] def register = {
    assert(!registered)
    registered = true
  }

  // TODO: rewrite keySet/indexSet

  private var keyBuffer = Set[Key[P]]()
  private var indexBuffer = Set[Index[P]]()

  /** the keys for this root type. you populate this set by repeatedly calling either of the
   * `PType.key` methods in your class initializer. you should only attempt to access this set
   * after your `PType` is fully initialized.
   * @throws longevity.exceptions.subdomain.root.EarlyKeyAccessException on attempt to access
   * this set before the `PType` is fully initialized
   */
  lazy val keySet: Set[Key[P]] = {
    if (!registered) throw new EarlyKeyAccessException
    keyBuffer
  }

  /** the indexes for this persistent type. you populate this set by repeatedly
   * calling either of the `PType.index` methods in your class initializer. you
   * should only attempt to access this set after your `PType` is fully
   * initialized.
   * @throws longevity.exceptions.subdomain.root.EarlyIndexAccessException on
   * attempt to access this set before the `PType` is fully initialized
   */
  lazy val indexSet: Set[Index[P]] = {
    if (!registered) throw new EarlyIndexAccessException
    indexBuffer
  }

  /** constructs a [[longevity.subdomain.root.Prop]] from a path
   * @throws longevity.exceptions.subdomain.root.PropException if any step along
   * the path does not exist, or any non-final step along the path is not an
   * entity, or the final step along the path is not a [[Shorthand]], an
   * [[Assoc]] or a basic type
   * @see `emblem.basicTypes`
   */
  def prop[A : TypeKey](path: String): Prop[P, A] = Prop(path, emblem, entityTypeKey, shorthandPool)

  /** constructs a key for this persistent type based on the supplied set of key props
   * @param propsHead one of the properties that define this key
   * @param propsTail any remaining properties that define this key
   * @throws longevity.exceptions.subdomain.root.LateKeyDefException on attempt
   * to create a new key after the `PType` is fully initialized
   */
  def key(propsHead: Prop[P, _], propsTail: Prop[P, _]*): Key[P] = {
    if (registered) throw new LateKeyDefException
    val key = Key(propsHead :: propsTail.toList)
    keyBuffer += key
    key
  }

  /** constructs a index for this persistent type based on the supplied set of index props
   * 
   * @param propsHead one of the properties that define this index
   * @param propsTail any remaining properties that define this index
   * @throws longevity.exceptions.subdomain.root.LateIndexDefException on
   * attempt to create a new index after the `PType` is fully initialized
   */
  def index(propsHead: Prop[P, _], propsTail: Prop[P, _]*): Index[P] = {
    if (registered) throw new LateIndexDefException
    val index = Index(propsHead :: propsTail.toList)
    indexBuffer += index
    index
  }

  /** contains implicit imports to make the query DSL work */
  lazy val queryDsl = new QueryDsl[P]
  
}
