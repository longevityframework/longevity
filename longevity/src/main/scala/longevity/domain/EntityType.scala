package longevity.domain

import scala.language.higherKinds
import scala.reflect.runtime.universe._
import emblem._

/** an entity type.
 * TODO scaladoc */
trait EntityType[E <: Entity] {

  def emblem: Emblem[E]

  // TODO: what about lists? other traversables? do i need to halt at a fixed set of supported traversables?
  // can i use CanBuildFrom somehow to generalize?

  // TODO: can i get rid of these annoying casts?

  private[longevity] val assocProps: Seq[EmblemProp[E, Assoc[_ <: Entity]]] = {
    emblem.props.flatMap { prop =>
      if (prop.typeTag.tpe <:< typeTag[Assoc[_]].tpe)
        Some(prop.asInstanceOf[EmblemProp[E, Assoc[_ <: Entity]]])
      else
        None
    }
  }

  private[longevity] val assocSetProps: Seq[EmblemProp[E, Set[Assoc[_ <: Entity]]]] = {
    emblem.props.flatMap { prop =>
      if (
        prop.typeTag.tpe <:< typeTag[Set[_]].tpe &&
        prop.typeTag.tpe.typeArgs.head <:< typeTag[Assoc[_]].tpe)
        Some(prop.asInstanceOf[EmblemProp[E, Set[Assoc[_ <: Entity]]]])
      else
        None
    }
  }

  // TODO: definitely need some tests to cover options

  private[longevity] val assocOptionProps: Seq[EmblemProp[E, Option[Assoc[_ <: Entity]]]] = {
    emblem.props.flatMap { prop =>
      if (prop.typeTag.tpe <:< typeTag[Option[Assoc[_]]].tpe)
        Some(prop.asInstanceOf[EmblemProp[E, Option[Assoc[_ <: Entity]]]])
      else
        None
    }
  }

}
