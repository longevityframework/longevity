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
  implicit private val rootTypeKey: TypeKey[E], // TODO: cant i use entityTypeKey??
  implicit private val shorthandPool: ShorthandPool)
extends EntityType[E] {

  /** constructs a [[NatKeyProp]] from a path
   * @throws InvalidNatKeyPropPathException if any step along the path does not exist
   * @throws InvalidNatKeyPropPathException if any non-final step along the path is not an entity
   * @throws InvalidNatKeyPropPathException if the final step along the path is not an [[Assoc]] or a basic type
   * @see `emblem.basicTypes`
   */
  def natKeyProp(path: String): NatKeyProp[E] = NatKeyProp(path, emblem, entityTypeKey, shorthandPool)

  /** a natural key for this root entity type
   * @param props the set of nat key properties that make up this natural key
   */
  case class NatKey private (val props: Set[NatKeyProp[E]]) {

    private lazy val propPathToProp = props.map(p => p.path -> p).toMap

    def builder = new ValBuilder

    class Val private[NatKey] (private val propVals: Map[NatKeyProp[E], Any]) {

      def apply(prop: NatKeyProp[E]): Any = propVals(prop)

      def apply(propPath: String): Any = propVals(propPathToProp(propPath))
    }

    class ValBuilder {

      private var propVals = Map[NatKeyProp[E], Any]()

      def setProp[A : TypeKey](propPath: String, propVal: A): Unit = setProp(propPathToProp(propPath), propVal)

      def setProp[A : TypeKey](prop: NatKeyProp[E], propVal: A): Unit = {
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
      new NatKey(propPaths.map(natKeyProp(_)))
    }

    /** constructs a natural key for this root entity type based on the supplied set of nat key props.
     * @param propsHead one of the properties that define this nat key
     * @param propsTail any remaining properties that define this nat key
     */
    def apply(propsHead: NatKeyProp[E], propsTail: NatKeyProp[E]*): NatKey = {
      new NatKey(propsTail.toSet + propsHead)
    }

  }

}
