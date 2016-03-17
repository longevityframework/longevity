package longevity.subdomain

import emblem.basicTypes.isBasicType
import emblem.imports._
import longevity.subdomain.ptype._

/** a type class for a domain entity that is stored in a persistent collection */
abstract class PType[
  P <: Persistent](
  implicit private val pTypeKey: TypeKey[P],
  implicit private val shorthandPool: ShorthandPool = ShorthandPool.empty)
extends EntityType[P] {

  /** the keys for this persistent type */
  val keySet: Set[Key[P]]

  /** the indexes for this persistent type */
  val indexSet: Set[Index[P]]

  /** constructs a [[longevity.subdomain.ptype.Prop Prop]] from a path
   * 
   * @throws longevity.exceptions.subdomain.ptype.PropException if any step along
   * the path does not exist, or any non-final step along the path is not an
   * entity, or the final step along the path is not a [[Shorthand]], an
   * [[Assoc]] or a basic type.
   * 
   * @see `emblem.basicTypes`
   */
  def prop[A : TypeKey](path: String): Prop[P, A] = Prop(path, emblem, entityTypeKey, shorthandPool)

  /** constructs a key for this persistent type based on the supplied set of key props
   * 
   * @param propsHead the first of the properties that define this key
   * @param propsTail any remaining properties that define this key
   */
  def key(propsHead: Prop[P, _], propsTail: Prop[P, _]*): Key[P] =
    Key(propsHead :: propsTail.toList)

  /** uses runtime reflection to scan the provided container for keys. returns
   * the keys in a set. this scan is shallow; it will not find keys in objects
   * or values embedded within the container.
   *
   * the intended usage is to allow you to easily define your [[keySet]] by
   * scanning an object with the keys. for example:
   *
   * {{{
   * object User extends RootType[User] {
   *   object props {
   *     val userId = prop[String]("userId")
   *     val email = prop[String]("email")
   *   }
   *   object keys {
   *     val userId = key(props.userId)
   *     val email = key(props.email)
   *   }
   *   val keySet = kscan(keys)
   *   val indexSet = Set()
   * }
   * }}}
   * 
   * @param kcontainer the container to scan
   */
  def kscan(kcontainer: AnyRef): Set[Key[P]] = ???

  /** an empty key set */
  def emptyKeySet = Set[Key[P]]()

  /** constructs an index for this persistent type based on the supplied set of index props
   * 
   * @param propsHead the first of the properties that define this index
   * @param propsTail any remaining properties that define this index
   */
  def index(propsHead: Prop[P, _], propsTail: Prop[P, _]*): Index[P] =
    Index(propsHead :: propsTail.toList)

  /** uses runtime reflection to scan the provided container for indexes.
   * returns the indexes in a set. this scan is shallow; it will not find
   * indexes in objects or values embedded within the container.
   *
   * the intended usage is to allow you to easily define your [[indexSet]] by
   * scanning an object with the indexes. for example:
   *
   * {{{
   * object User extends RootType[User] {
   *   object props {
   *     val userId = prop[String]("userId")
   *     val email = prop[String]("email")
   *   }
   *   val keySet = Set()
   *   object indexes {
   *     val userId = key(props.userId)
   *     val email = key(props.email)
   *   }
   *   val indexSet = iscan(indexes)
   * }
   * }}}
   * 
   * @param icontainer the container to scan
   */
  def iscan(icontainer: AnyRef): Set[Index[P]] = ???

  /** an empty index set */
  def emptyIndexSet = Set[Index[P]]()

  /** contains implicit imports to make the query DSL work */
  lazy val queryDsl = new QueryDsl[P]

}
