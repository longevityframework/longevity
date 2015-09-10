package longevity.subdomain

import emblem.basicTypes.isBasicType
import emblem.imports._
import longevity.exceptions.InvalidNatKeyPropPathException
import longevity.exceptions.NatKeyDoesNotContainPropException
import longevity.exceptions.NatKeyPropValTypeMismatchException
import longevity.exceptions.UnsetNatKeyPropException

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
  case class NatKeyProp private (
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

      def validLeafNatKeyPropType(key: TypeKey[_]): Boolean =
        isBasicType(key) || key <:< typeKey[Assoc[_]] || shorthandPool.contains(key)

      val natKeyPropTypeKey = leafInfo._1.typeKey
      if (!validLeafNatKeyPropType(natKeyPropTypeKey))
        throw new InvalidNatKeyPropPathException(
          s"nat key prop path $path for root ${rootTypeKey.name} is not a basic type, shorthand, or an assoc")

      new NatKeyProp(path, natKeyPropTypeKey)
    }
  }

  /** a natural key for this root entity type
   * @param props the set of nat key properties that make up this natural key
   */
  case class NatKey private (val props: Set[NatKeyProp]) {

    private lazy val propPathToProp = props.map(p => p.path -> p).toMap

    def builder = new ValBuilder

    class Val private[NatKey] (private val propVals: Map[NatKeyProp, Any]) {

      def apply(prop: NatKeyProp): Any = propVals(prop)

      def apply(propPath: String): Any = propVals(propPathToProp(propPath))
    }

    class ValBuilder {

      private var propVals = Map[NatKeyProp, Any]()

      def setProp[A : TypeKey](propPath: String, propVal: A): Unit = setProp(propPathToProp(propPath), propVal)

      def setProp[A : TypeKey](prop: NatKeyProp, propVal: A): Unit = {
        if (!props.contains(prop)) throw new NatKeyDoesNotContainPropException(NatKey.this, prop)
        if (! (typeKey[A] <:< prop.typeKey)) throw new NatKeyPropValTypeMismatchException(prop, propVal)
        propVals += prop -> propVal
      }

      def build: Val = {
        if (propVals.size < props.size) {
          throw new UnsetNatKeyPropException(NatKey.this, props -- propVals.keys)
        }
        new Val(propVals)
      }
    }

  }

  object NatKey {

    /** constructs a natural key for this root entity type based on the supplied set of property paths.
     * @param propPathHead one of the property paths for the properties that define this nat key
     * @param propPathTail any remaining property paths for the properties that define this nat key
     * @throws InvalidNatKeyPropPathException if any of the supplied property paths are invalid
     * @see NatKeyProp.apply
     */
    def apply(propPathHead: String, propPathTail: String*): NatKey = {
      val propPaths = propPathTail.toSet + propPathHead
      new NatKey(propPaths.map(NatKeyProp(_)))
    }

    /** constructs a natural key for this root entity type based on the supplied set of nat key props.
     * @param propsHead one of the properties that define this nat key
     * @param propsTail any remaining properties that define this nat key
     */
    def apply(propsHead: NatKeyProp, propsTail: NatKeyProp*): NatKey = {
      new NatKey(propsTail.toSet + propsHead)
    }

  }

}
