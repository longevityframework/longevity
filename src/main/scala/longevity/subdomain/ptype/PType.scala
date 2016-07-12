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

  /** the keys for this persistent type */
  lazy val keySet: Set[AnyKey[P]] = kscan("keys")

  /** the indexes for this persistent type */
  lazy val indexSet: Set[Index[P]] = iscan("indexes")

  /** constructs a [[longevity.subdomain.ptype.Prop Prop]] of type `A` from the
   * provided property path.
   *
   * the provided type `A` should match the type of the actual property in the
   * `Persistent`. type `A` should not contain any collections or
   * [[longevity.subdomain.embeddable.PolyType poly types]]. violations will
   * cause an exception to be thrown on [[longevity.subdomain.Subdomain
   * Subdomain construction]].
   *
   * @tparam A the type of the property
   * @param path the property path
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
