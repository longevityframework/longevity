package longevity.ddd.subdomain

import emblem.TypeKey
import longevity.subdomain.embeddable.EType

/** a type class for a [[Entity domain entity]] */
abstract class EntityType[E <: Entity : TypeKey] extends EType[E]

/** contains a factory method for creating `EntityTypes` */
object EntityType {

  /** create and return an `EntityType` for type `E` */
  def apply[E <: Entity : TypeKey] = new EntityType[E] {
  }

}
