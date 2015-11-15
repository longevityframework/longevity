package longevity.subdomain

import emblem.exceptions.NoSuchPropertyException
import emblem.exceptions.NonEmblemInPropPathException
import emblem.exceptions.EmptyPropPathException
import emblem.basicTypes.isBasicType
import emblem.EmblemPropPath
import emblem.imports._
import longevity.exceptions.subdomain.EmptyNatKeyPropPathException
import longevity.exceptions.subdomain.NoSuchNatKeyPropPathSegmentException
import longevity.exceptions.subdomain.NonEntityNatKeyPropPathSegmentException
import longevity.exceptions.subdomain.InvalidNatKeyPropPathLeafException

/** a property of the root that can be used as part of a natural key. the property can belong to a
 * contained entity of the root at any depth of containment, so long as every containment step along the
 * path is exactly-one. the type of the property must be a [[Assoc]], a [[Shorthand]], or a basic type.
 * 
 * @param path a dot-separated path of the property descending from the root entity
 * @param typeKey the `TypeKey` for the property value type
 * @see `emblem.basicTypes`
 */
case class NatKeyProp[E <: RootEntity] private (
  val path: String,
  val typeKey: TypeKey[_])(
  private val emblemPropPath: EmblemPropPath[E, _]) {

  /** the value of this property for a specific root entity
   * @param e the root entity we are looking up the value of the property for
   */
  def natKeyPropVal(e: E): Any = emblemPropPath.get(e)

}

object NatKeyProp {

  private[subdomain] def apply[E <: RootEntity](
    path: String,
    emblem: Emblem[E],
    rootTypeKey: TypeKey[E],
    shorthandPool: ShorthandPool)
  : NatKeyProp[E] = {

    def validatePath(): EmblemPropPath[E, _] =
      try {
        EmblemPropPath.unbounded(emblem, path)
      } catch {
        case e: EmptyPropPathException =>
          throw new EmptyNatKeyPropPathException(e)
        case e: NoSuchPropertyException =>
          throw new NoSuchNatKeyPropPathSegmentException(e.propName, path, rootTypeKey, e)
        case e: NonEmblemInPropPathException =>
          throw new NonEntityNatKeyPropPathSegmentException(e.nonEmblemPathSegment, path, rootTypeKey, e)
      }

    def validateNonLeafEmblemProps(nonLeafEmblemProps: Seq[EmblemProp[_ <: HasEmblem, _]]): Unit =
      nonLeafEmblemProps foreach { nonLeafEmblemProp =>
        if (!(nonLeafEmblemProp.typeKey <:< typeKey[Entity]))
          throw new NonEntityNatKeyPropPathSegmentException(nonLeafEmblemProp.name, path, rootTypeKey)
      }

    def validateLeafEmblemProp(leafEmblemProp: EmblemProp[_ <: HasEmblem, _]): TypeKey[_] = {
      val key = leafEmblemProp.typeKey
      if (!(isBasicType(key) || key <:< typeKey[Assoc[_]] || shorthandPool.contains(key)))
        throw new InvalidNatKeyPropPathLeafException(path, rootTypeKey)
      key
    }

    val emblemPropPath = validatePath()
    val emblemProps = emblemPropPath.props

    val nonLeafEmblemProps = emblemProps.dropRight(1)
    val () = validateNonLeafEmblemProps(nonLeafEmblemProps)

    val leafEmblemProp = emblemProps.last
    val natKeyPropTypeKey = validateLeafEmblemProp(leafEmblemProp)
    new NatKeyProp(path, natKeyPropTypeKey)(emblemPropPath)
  }

}
