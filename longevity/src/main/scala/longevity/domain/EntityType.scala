package longevity.domain

import scala.reflect.runtime.universe._
import emblem._

/** an entity type.
 * TODO scaladoc */
abstract class EntityType[E <: Entity : TypeKey] {

  lazy val entityTypeKey: TypeKey[E] = typeKey[E]

  lazy val emblem: Emblem[E] = emblemFor[E]

  // TODO: what about lists? other traversables? do i need to halt at a fixed set of supported traversables?
  // can i use CanBuildFrom somehow to generalize?

  // TODO: can i get rid of these annoying casts?

  private[longevity] val assocProps: Seq[EmblemProp[E, Assoc[_ <: Entity]]] = {
    emblem.props.flatMap { prop =>
      if (prop.typeKey.tag.tpe <:< typeOf[Assoc[_]])
        Some(prop.asInstanceOf[EmblemProp[E, Assoc[_ <: Entity]]])
      else
        None
    }
  }

  private[longevity] val assocSetProps: Seq[EmblemProp[E, Set[Assoc[_ <: Entity]]]] = {
    emblem.props.flatMap { prop =>
      if (
        prop.typeKey.tag.tpe <:< typeOf[Set[_]] &&
        prop.typeKey.tag.tpe.typeArgs.head <:< typeOf[Assoc[_]])
        Some(prop.asInstanceOf[EmblemProp[E, Set[Assoc[_ <: Entity]]]])
      else
        None
    }
  }

  // TODO: definitely need some tests to cover options

  private[longevity] val assocOptionProps: Seq[EmblemProp[E, Option[Assoc[_ <: Entity]]]] = {
    emblem.props.flatMap { prop =>
      if (prop.typeKey.tag.tpe <:< typeOf[Option[Assoc[_]]])
        Some(prop.asInstanceOf[EmblemProp[E, Option[Assoc[_ <: Entity]]]])
      else
        None
    }
  }

}
