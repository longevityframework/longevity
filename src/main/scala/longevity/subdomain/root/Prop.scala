package longevity.subdomain.root

import emblem.EmblemPropPath
import emblem.basicTypes.basicTypeOrderings
import emblem.basicTypes.isBasicType
import emblem.exceptions.EmblemPropPathTypeMismatchException
import emblem.exceptions.NoSuchPropertyException
import emblem.exceptions.NonEmblemInPropPathException
import emblem.exceptions.EmptyPropPathException
import emblem.imports._
import longevity.exceptions.subdomain.root.UnsupportedPropTypeException
import longevity.exceptions.subdomain.root.NoSuchPropException
import longevity.exceptions.subdomain.root.PropNotOrderedException
import longevity.exceptions.subdomain.root.PropTypeException
import longevity.subdomain._

/** a property for this root type. properties can be used to define [[Key keys]] and [[Index indexes]],
 * as well as for building [[Query queries]]. a property can descend from the root into child entities at any
 * depth. at present, a property cannot pass through any collections. at present, the type of the property must
 * be an [[Assoc]], a [[Shorthand]], or a basic type.
 * 
 * @param path a dot-separated path of the property descending from the root
 * @param typeKey the `TypeKey` for the property value type
 * @see `emblem.basicTypes`
 */
case class Prop[R <: Root, A] private (
  val path: String,
  val typeKey: TypeKey[A])(
  private val emblemPropPath: EmblemPropPath[R, A]) {

  /** the value of this property for a specific root
   * @param e the root we are looking up the value of the property for
   */
  def propVal(r: R): A = emblemPropPath.get(r)

  /** an ordering for property values
   * 
   * @throws longevity.exceptions.subdomain.root.PropNotOrderedException
   * if the ordering is accessed, and the property value type is not a type
   * for which orderings are supported. right now, we only support orderings
   * for basic types
   */
  lazy val ordering: Ordering[A] =
    if (isBasicType(typeKey))
      basicTypeOrderings(typeKey)
    else
      throw new PropNotOrderedException(this)

  override def toString: String = path

}

object Prop {

  private[subdomain] def apply[R <: Root, A : TypeKey](
    path: String,
    emblem: Emblem[R],
    rootTypeKey: TypeKey[R],
    shorthandPool: ShorthandPool)
  : Prop[R, A] = {
    val prop = unbounded(path, emblem, rootTypeKey, shorthandPool)
    if (!(typeKey[A] <:< prop.typeKey)) throw new PropTypeException(path, rootTypeKey, typeKey[A])
    prop.asInstanceOf[Prop[R, A]]
  }

  private[subdomain] def unbounded[R <: Root](
    path: String,
    emblem: Emblem[R],
    rootTypeKey: TypeKey[R],
    shorthandPool: ShorthandPool)
  : Prop[R, _] = {

    def validatePath(): EmblemPropPath[R, _] =
      try {
        EmblemPropPath.unbounded(emblem, path)
      } catch {
        case e: EmptyPropPathException =>
          throw new NoSuchPropException(path, rootTypeKey)
        case e: NoSuchPropertyException =>
          throw new NoSuchPropException(path, rootTypeKey)
        case e: NonEmblemInPropPathException[_] =>
          throw new UnsupportedPropTypeException(path)(rootTypeKey, e.typeKey)
      }

    def validateNonLeafEmblemProps(nonLeafEmblemProps: Seq[EmblemProp[_ <: HasEmblem, _]]): Unit =
      nonLeafEmblemProps foreach { nonLeafEmblemProp =>
        if (!(nonLeafEmblemProp.typeKey <:< typeKey[Entity]))
          throw new UnsupportedPropTypeException(path)(rootTypeKey, nonLeafEmblemProp.typeKey)
      }

    def validateLeafEmblemProp(leafEmblemProp: EmblemProp[_ <: HasEmblem, _]): TypeKey[_] = {
      val key = leafEmblemProp.typeKey
      if (!(isBasicType(key) || key <:< typeKey[Assoc[_]] || shorthandPool.contains(key)))
        throw new UnsupportedPropTypeException(path)(rootTypeKey, key)
      key
    }

    val emblemPropPath = validatePath()
    val emblemProps = emblemPropPath.props

    val nonLeafEmblemProps = emblemProps.dropRight(1)
    val () = validateNonLeafEmblemProps(nonLeafEmblemProps)

    val leafEmblemProp = emblemProps.last
    val propTypeKey = validateLeafEmblemProp(leafEmblemProp)

    def newProp[A : TypeKey](path: String, propTypeKey: TypeKey[A], emblemPropPath: EmblemPropPath[R, _]) =
      new Prop(path, propTypeKey)(emblemPropPath.asInstanceOf[EmblemPropPath[R, A]])

    newProp(path, propTypeKey, emblemPropPath)
  }

}
