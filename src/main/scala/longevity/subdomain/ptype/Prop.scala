package longevity.subdomain.ptype

import emblem.EmblemPropPath
import emblem.basicTypes.basicTypeOrderings
import emblem.basicTypes.isBasicType
import emblem.exceptions.EmblemPropPathTypeMismatchException
import emblem.exceptions.NoSuchPropertyException
import emblem.exceptions.NonEmblemInPropPathException
import emblem.exceptions.EmptyPropPathException
import emblem.imports._
import longevity.exceptions.subdomain.ptype.UnsupportedPropTypeException
import longevity.exceptions.subdomain.ptype.NoSuchPropException
import longevity.exceptions.subdomain.ptype.PropNotOrderedException
import longevity.exceptions.subdomain.ptype.PropTypeException
import longevity.subdomain._

/** a property for this persistent type. properties can be used to define [[Key keys]]
 * and [[Index indexes]], as well as for building [[Query queries]]. a property
 * can descend from the root into child entities at any depth. at present, a
 * property cannot pass through any collections. at present, the type of the
 * property must be an [[Assoc]], a [[Shorthand]], or a basic type.
 * 
 * @param path a dot-separated path of the property descending from the root
 * @param typeKey the `TypeKey` for the property value type
 * @see `emblem.basicTypes`
 */
case class Prop[P <: Persistent, A] private (
  path: String,
  typeKey: TypeKey[A])(
  private val emblemPropPath: EmblemPropPath[P, A],
  private val shorthandPool: ShorthandPool) {

  /** the value of this property for a persistent
   * @param p the persistent we are looking up the value of the property for
   */
  def propVal(p: P): A = emblemPropPath.get(p)

  /** an ordering for property values
   * 
   * @throws longevity.exceptions.subdomain.ptype.PropNotOrderedException
   * if the ordering is accessed, and the property value type is not a type
   * for which orderings are supported. right now, we only support orderings
   * for basic types
   */
  lazy val ordering: Ordering[A] =
    if (isBasicType(typeKey)) {
      basicTypeOrderings(typeKey)
    } else if (shorthandPool.contains(typeKey)) {
      shorthandPool(typeKey).actualOrdering
    } else {
      throw new PropNotOrderedException(this)
    }

  override def toString: String = path

}

object Prop {

  private[subdomain] def apply[P <: Persistent, A : TypeKey](
    path: String,
    emblem: Emblem[P],
    pTypeKey: TypeKey[P],
    shorthandPool: ShorthandPool)
  : Prop[P, A] = {
    val prop = unbounded(path, emblem, pTypeKey, shorthandPool)
    if (!(typeKey[A] <:< prop.typeKey)) throw new PropTypeException(path, pTypeKey, typeKey[A])
    prop.asInstanceOf[Prop[P, A]]
  }

  private[subdomain] def unbounded[P <: Persistent](
    path: String,
    emblem: Emblem[P],
    pTypeKey: TypeKey[P],
    shorthandPool: ShorthandPool)
  : Prop[P, _] = {

    def validatePath(): EmblemPropPath[P, _] =
      try {
        EmblemPropPath.unbounded(emblem, path)
      } catch {
        case e: EmptyPropPathException =>
          throw new NoSuchPropException(path, pTypeKey)
        case e: NoSuchPropertyException =>
          throw new NoSuchPropException(path, pTypeKey)
        case e: NonEmblemInPropPathException[_] =>
          throw new UnsupportedPropTypeException(path)(pTypeKey, e.typeKey)
      }

    def validateNonLeafEmblemProps(nonLeafEmblemProps: Seq[EmblemProp[_ <: HasEmblem, _]]): Unit =
      nonLeafEmblemProps foreach { nonLeafEmblemProp =>
        if (!(nonLeafEmblemProp.typeKey <:< typeKey[Entity]))
          throw new UnsupportedPropTypeException(path)(pTypeKey, nonLeafEmblemProp.typeKey)
      }

    def validateLeafEmblemProp(leafEmblemProp: EmblemProp[_ <: HasEmblem, _]): TypeKey[_] = {
      val key = leafEmblemProp.typeKey
      if (!(isBasicType(key) || key <:< typeKey[Assoc[_]] || shorthandPool.contains(key)))
        throw new UnsupportedPropTypeException(path)(pTypeKey, key)
      key
    }

    val emblemPropPath = validatePath()
    val emblemProps = emblemPropPath.props

    val nonLeafEmblemProps = emblemProps.dropRight(1)
    val () = validateNonLeafEmblemProps(nonLeafEmblemProps)

    val leafEmblemProp = emblemProps.last
    val propTypeKey = validateLeafEmblemProp(leafEmblemProp)

    def newProp[A : TypeKey](
      path: String,
      propTypeKey: TypeKey[A],
      emblemPropPath: EmblemPropPath[P, _]) =
      new Prop(
        path,
        propTypeKey)(
        emblemPropPath.asInstanceOf[EmblemPropPath[P, A]],
        shorthandPool)

    newProp(path, propTypeKey, emblemPropPath)
  }

}
