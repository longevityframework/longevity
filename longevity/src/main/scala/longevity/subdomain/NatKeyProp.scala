package longevity.subdomain

import emblem.exceptions.NoSuchPropertyException
import emblem.exceptions.NonEmblemInPropPathException
import emblem.exceptions.EmptyPropPathException
import emblem.basicTypes.isBasicType
import emblem.EmblemPropPath
import emblem.imports._
import longevity.exceptions.InvalidNatKeyPropPathException

/** a property of the root that can be used as part of a natural key. the property can belong to a
 * contained entity of the root at any depth of containment, so long as every containment step along the
 * path is exactly-one. the type of the property must be a [[Assoc]] or a basic type.
 * 
 * @param path a dot-separated path of the property descending from the root entity
 * @param typeKey [[TypeKey type key]] for the property value type
 * @see `emblem.basicTypes`
 */
// TODO should i incorporate the resulting type into the type for NatKeyProp?
case class NatKeyProp[E <: RootEntity] private (
  val path: String,
  val typeKey: TypeKey[_])(
  private val emblemPropPath: EmblemPropPath[E, _]) {

  // TODO scaladocs in this file

  def natKeyPropVal(e: E): Any = emblemPropPath.get(e)

}

object NatKeyProp {

  private[subdomain] def apply[E <: RootEntity](
    path: String,
    emblem: Emblem[E],
    rootTypeKey: TypeKey[E],
    shorthandPool: ShorthandPool)
  : NatKeyProp[E] = {

    // TODO build out fuckin hierarchy for InvalidNatKeyPropPathException

    def validatePath(): EmblemPropPath[E, _] =
      try {
        EmblemPropPath.unbounded(emblem, path)
      } catch {
        case e: EmptyPropPathException =>
          throw new InvalidNatKeyPropPathException("empty nat key prop path", e)
        case e: NoSuchPropertyException =>
          throw new InvalidNatKeyPropPathException(
            s"path segment ${e.propName} does not specify a property in path $path for root ${rootTypeKey.name}",
            e)
        case e: NonEmblemInPropPathException =>
          throw new InvalidNatKeyPropPathException(
            s"non-leaf path segment ${e.nonEmblemPathSegment} is not an entity in path $path " +
            s"for root ${rootTypeKey.name}",
            e)
      }

    def validateNonLeafEmblemProps(nonLeafEmblemProps: Seq[EmblemProp[_ <: HasEmblem, _]]): Unit =
      nonLeafEmblemProps foreach { nonLeafEmblemProp =>
        if (!(nonLeafEmblemProp.typeKey <:< typeKey[Entity]))
          throw new InvalidNatKeyPropPathException(
            s"non-leaf path segment ${nonLeafEmblemProp.name} is not an entity in path $path " +
            s"for root ${rootTypeKey.name}")
      }

    def validateLeafEmblemProp(leafEmblemProp: EmblemProp[_ <: HasEmblem, _]): TypeKey[_] = {
      val key = leafEmblemProp.typeKey
      if (!(isBasicType(key) || key <:< typeKey[Assoc[_]] || shorthandPool.contains(key)))
        throw new InvalidNatKeyPropPathException(
          s"nat key prop path $path for root ${rootTypeKey.name} is not a basic type, shorthand, or an assoc")
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
