package longevity.subdomain.ptype

import emblem.Emblematic
import emblem.TypeKey
import emblem.typeKey
import emblem.basicTypes.isBasicType
import emblem.reflectionUtil.innerModule
import emblem.reflectionUtil.termsWithType
import longevity.exceptions.subdomain.ptype.NoIndexesForPTypeException
import longevity.exceptions.subdomain.ptype.NoKeysForPTypeException
import longevity.subdomain.ShorthandPool
import longevity.subdomain.persistent.Persistent

/** a type class for a domain element that is stored in a persistent collection
 *
 * @tparam P the persistent type
 */
abstract class PType[P <: Persistent : TypeKey](
  implicit private val shorthandPool: ShorthandPool = ShorthandPool.empty) {

  /** the type key for the persistent type */
  val pTypeKey = typeKey[P]

  private val propLateInitializer = new PropLateInitializer[P]

  /** the keys for this persistent type */
  lazy val keySet: Set[Key[P]] = kscan("keys")

  /** the indexes for this persistent type */
  lazy val indexSet: Set[Index[P]] = iscan("indexes")

  /** constructs a [[longevity.subdomain.ptype.Prop Prop]] from a path
   *
   * @throws longevity.exceptions.subdomain.ptype.PropException if any step along
   * the path does not exist, or any non-final step along the path is not an
   * entity, or the final step along the path is not a [[Shorthand]], an
   * [[Assoc]] or a basic type.
   *
   * @see `emblem.basicTypes`
   */
  def prop[A : TypeKey](path: String): Prop[P, A] =
    Prop(path, pTypeKey, typeKey[A])(shorthandPool, propLateInitializer)

  /** constructs a key for this persistent type based on the supplied set of key props
   *
   * @param propsHead the first of the properties that define this key
   * @param propsTail any remaining properties that define this key
   */
  def key(propsHead: Prop[P, _], propsTail: Prop[P, _]*): Key[P] =
    Key(propsHead :: propsTail.toList)

  /** constructs an index for this persistent type based on the supplied set of
   * index props
   * 
   * @param propsHead the first of the properties that define this index
   * @param propsTail any remaining properties that define this index
   */
  def index(propsHead: Prop[P, _], propsTail: Prop[P, _]*): Index[P] =
    Index(propsHead :: propsTail.toList)

  /** contains implicit imports to make the query DSL work */
  lazy val queryDsl = new QueryDsl[P]

  // intended for use by the `Subdomain`. throws exception if called more than once
  private[subdomain] def registerEmblematic(emblematic: Emblematic): Unit = {
    propLateInitializer.registerEmblematic(emblematic)
  }

  private def kscan(containerName: String): Set[Key[P]] = {
    val keys: Any = innerModule(this, "keys").getOrElse {
      throw new NoKeysForPTypeException
    }
    implicit val tag = pTypeKey.tag
    termsWithType[Key[P]](keys)
  }

  private def iscan(containerName: String): Set[Index[P]] = {
    val indexes: Any = innerModule(this, "indexes").getOrElse {
      throw new NoIndexesForPTypeException
    }
    implicit val tag = pTypeKey.tag
    termsWithType[Index[P]](indexes)
  }

}
