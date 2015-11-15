package longevity.subdomain

import emblem.exceptions.NoSuchPropertyException
import emblem.exceptions.NonEmblemInPropPathException
import emblem.exceptions.EmptyPropPathException
import emblem.basicTypes.isBasicType
import emblem.EmblemPropPath
import emblem.imports._
import longevity.exceptions.subdomain.EmptyKeyPropPathException
import longevity.exceptions.subdomain.NoSuchKeyPropPathSegmentException
import longevity.exceptions.subdomain.NonEntityKeyPropPathSegmentException
import longevity.exceptions.subdomain.InvalidKeyPropPathLeafException

/** a property of the root that can be used as part of a natural key. the property can belong to a
 * contained entity of the root at any depth of containment, so long as every containment step along the
 * path is exactly-one. the type of the property must be a [[Assoc]], a [[Shorthand]], or a basic type.
 * 
 * @param path a dot-separated path of the property descending from the root entity
 * @param typeKey the `TypeKey` for the property value type
 * @see `emblem.basicTypes`
 */
case class KeyProp[E <: RootEntity] private (
  val path: String,
  val typeKey: TypeKey[_])(
  private val emblemPropPath: EmblemPropPath[E, _]) {

  /** the value of this property for a specific root entity
   * @param e the root entity we are looking up the value of the property for
   */
  def keyPropVal(e: E): Any = emblemPropPath.get(e)

}

object KeyProp {

  private[subdomain] def apply[E <: RootEntity](
    path: String,
    emblem: Emblem[E],
    rootTypeKey: TypeKey[E],
    shorthandPool: ShorthandPool)
  : KeyProp[E] = {

    def validatePath(): EmblemPropPath[E, _] =
      try {
        EmblemPropPath.unbounded(emblem, path)
      } catch {
        case e: EmptyPropPathException =>
          throw new EmptyKeyPropPathException(e)
        case e: NoSuchPropertyException =>
          throw new NoSuchKeyPropPathSegmentException(e.propName, path, rootTypeKey, e)
        case e: NonEmblemInPropPathException =>
          throw new NonEntityKeyPropPathSegmentException(e.nonEmblemPathSegment, path, rootTypeKey, e)
      }

    def validateNonLeafEmblemProps(nonLeafEmblemProps: Seq[EmblemProp[_ <: HasEmblem, _]]): Unit =
      nonLeafEmblemProps foreach { nonLeafEmblemProp =>
        if (!(nonLeafEmblemProp.typeKey <:< typeKey[Entity]))
          throw new NonEntityKeyPropPathSegmentException(nonLeafEmblemProp.name, path, rootTypeKey)
      }

    def validateLeafEmblemProp(leafEmblemProp: EmblemProp[_ <: HasEmblem, _]): TypeKey[_] = {
      val key = leafEmblemProp.typeKey
      if (!(isBasicType(key) || key <:< typeKey[Assoc[_]] || shorthandPool.contains(key)))
        throw new InvalidKeyPropPathLeafException(path, rootTypeKey)
      key
    }

    val emblemPropPath = validatePath()
    val emblemProps = emblemPropPath.props

    val nonLeafEmblemProps = emblemProps.dropRight(1)
    val () = validateNonLeafEmblemProps(nonLeafEmblemProps)

    val leafEmblemProp = emblemProps.last
    val keyPropTypeKey = validateLeafEmblemProp(leafEmblemProp)
    new KeyProp(path, keyPropTypeKey)(emblemPropPath)
  }

}
