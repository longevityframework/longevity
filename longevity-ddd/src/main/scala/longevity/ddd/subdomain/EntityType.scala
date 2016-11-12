package longevity.ddd.subdomain

import emblem.TypeKey
import longevity.subdomain.CType

/** a type class for a domain entity */
abstract class EntityType[E : TypeKey] extends CType[E]

/** contains a factory method for creating `EntityTypes` */
object EntityType {

  /** create and return an `EntityType` for type `E` */
  def apply[E : TypeKey] = new EntityType[E] {}

}
