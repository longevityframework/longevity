package longevity.subdomain.ptype

import emblem.TypeKey
import emblem.typeKey
import emblem.reflectionUtil.innerModule
import emblem.reflectionUtil.termsWithType
import longevity.exceptions.subdomain.ptype.NoIndexesForPTypeException
import longevity.exceptions.subdomain.ptype.NoKeysForPTypeException
import longevity.exceptions.subdomain.ptype.NoPropsForPTypeException
import longevity.subdomain.KeyVal
import longevity.subdomain.persistent.Persistent

/** a type class for a domain element that is stored in a persistent collection
 *
 * @tparam P the persistent type
 */
abstract class PType[P <: Persistent : TypeKey] {

  /** the type key for the persistent type */
  val pTypeKey = typeKey[P]

  /** the properties for this persistent type */
  lazy val propSet: Set[Prop[P, _]] = pscan("props")

  // TODO we need a named type for this crazy thing
  /** the keys for this persistent type */
  lazy val keySet: Set[AnyKey[P]] = kscan("keys")

  /** the indexes for this persistent type */
  lazy val indexSet: Set[Index[P]] = iscan("indexes")

  // TODO all these throws clauses have to move to subdomain construction
  /** constructs a [[longevity.subdomain.ptype.Prop Prop]] from a path
   *
   * @throws longevity.exceptions.subdomain.ptype.PropException if any step along
   * the path does not exist, or any non-final step along the path is not an
   * entity, or the final step along the path is not a basic type.
   *
   * TODO review above throws clause
   *
   * @see `emblem.emblematic.basicTypes`
   */
  def prop[A : TypeKey](path: String): Prop[P, A] = Prop(path, pTypeKey, typeKey[A])

  /** constructs a key for this persistent type
   *
   * @tparam V the type of the key value
   * @param keyValProp a property for the key
   */
  def key[V <: KeyVal[P, V] : TypeKey](keyValProp: Prop[P, V]): Key[P, V] = new Key(keyValProp)

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

  private def pscan(containerName: String): Set[Prop[P, _]] = {
    val props: Any = innerModule(this, "props").getOrElse {
      throw new NoPropsForPTypeException
    }
    implicit val tag = pTypeKey.tag
    termsWithType[Prop[P, _]](props)
  }

  private def kscan(containerName: String): Set[AnyKey[P]] = {
    val keys: Any = innerModule(this, "keys").getOrElse {
      throw new NoKeysForPTypeException
    }
    implicit val tag = pTypeKey.tag
    termsWithType[AnyKey[P]](keys)
  }

  private def iscan(containerName: String): Set[Index[P]] = {
    val indexes: Any = innerModule(this, "indexes").getOrElse {
      throw new NoIndexesForPTypeException
    }
    implicit val tag = pTypeKey.tag
    termsWithType[Index[P]](indexes)
  }

  override def toString = s"PType[${pTypeKey.name}]"
}
