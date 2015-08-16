package longevity.subdomain

import emblem.basicTypes.isBasicType
import emblem.imports._
import longevity.exceptions.InvalidNatKeyPropPathException

/** a type class for a domain entity that serves as an aggregate root */
abstract class RootEntityType[
  E <: RootEntity](
  implicit private val rootTypeKey: TypeKey[E],
  implicit private val shorthandPool: ShorthandPool)
extends EntityType[E] {

  // TODO pt-84760388 natural keys (in progress)

  /** a property of the root that can be used as part of a natural key. the property can belong to a
   * contained entity of the root at any depth of containment, so long as every containment step along the
   * path is exactly-one. the type of the property must be a [[Assoc]] or a basic type.
   * 
   * @param path a dot-separated path of the property descending from the root entity
   * @param typeKey [[TypeKey type key]] for the property value type
   * @see `emblem.basicTypes`
   */
  class NatKeyProp private (
    val path: String,
    val typeKey: TypeKey[_])

  object NatKeyProp {

    /** constructs a [[NatKeyProp]] from a path
     * @throws InvalidNatKeyPropPathException if any step along the path does not exist
     * @throws InvalidNatKeyPropPathException if any non-final step along the path is not an entity
     * @throws InvalidNatKeyPropPathException if the final step along the path is not an [[Assoc]] or a basic type
     * @see `emblem.basicTypes`
     */
    def apply(path: String): NatKeyProp = {
      val pathSegments = path.split('.')
      if (pathSegments.size == 0) throw new InvalidNatKeyPropPathException("empty nat key prop path")

      type PathSegmentInfo = (EmblemProp[_, _], String)
      def pathSegmentInfo(emblem: Emblem[_ <: HasEmblem], pathSegment: String): PathSegmentInfo = {
        val emblemProp = emblem.propMap.getOrElse(
          pathSegment,
          throw new InvalidNatKeyPropPathException(
            s"path segment $pathSegment does not specify a property in path $path for root ${rootTypeKey.name}"))
        (emblemProp, pathSegment)
      }

      val headInfo = pathSegmentInfo(emblem, pathSegments.head)

      val leafInfo = pathSegments.tail.foldLeft(headInfo) {
        case ((prop, prevSegment), segment) =>
          if (prop.typeKey <:< typeKey[Entity]) {
            val emblem = Emblem(prop.typeKey.asInstanceOf[TypeKey[Entity]])
            pathSegmentInfo(emblem, segment)
          } else {
            throw new InvalidNatKeyPropPathException(
              s"non-leaf path segment $prevSegment is not an entity in path $path for root ${rootTypeKey.name}")
          }
      }

      val natKeyPropTypeKey = leafInfo._1.typeKey
      if (!validLeafNatKeyPropType(natKeyPropTypeKey))
        throw new InvalidNatKeyPropPathException(
          s"nat key prop path $path for root ${rootTypeKey.name} is not a basic type, shorthand, or an assoc")

      new NatKeyProp(path, natKeyPropTypeKey)
    }
  }

  private def validLeafNatKeyPropType(key: TypeKey[_]): Boolean =
    isBasicType(key) || key <:< typeKey[Assoc[_]] || shorthandPool.contains(key)

}
