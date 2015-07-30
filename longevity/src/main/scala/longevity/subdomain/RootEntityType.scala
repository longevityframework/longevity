package longevity.subdomain

import emblem.imports._

/** a type class for a domain entity that serves as an aggregate root */
abstract class RootEntityType[
  E <: RootEntity](
  implicit rootTypeKey: TypeKey[E],
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
     * @throws InvalidNatKeyPropPath if any step along the path does not exist
     * @throws InvalidNatKeyPropPath if any step along the path is not an exactly-one containment
     * @throws InvalidNatKeyPropPath if the final step along the path is not an [[Assoc]] or a basic type
     * @see `emblem.basicTypes`
     */
    def apply(path: String): NatKeyProp = {
      val pathSegments = path.split('.')
      if (pathSegments.size == 1) {
        //val emblemProp = emblem.propMap.getOrElse(pathSegments(0), 
        new NatKeyProp(path, emblem.propMap(pathSegments(0)).typeKey)
      } else {
        // TODO
        new NatKeyProp(path, typeKey[String])
      }
    }
  }

}
