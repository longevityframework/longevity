package longevity.domain

import scala.reflect.runtime.universe._
import emblem._

/** a type class for a domain entity */
abstract class EntityType[E <: Entity : TypeKey] {

  lazy val entityTypeKey: TypeKey[E] = typeKey[E]

  lazy val emblem: Emblem[E] = emblemFor[E]

  // TODO pt-87441650 intra-entity contraints

  // all this assoc stuff below goes away with https://www.pivotaltracker.com/story/show/91219980

  private type AssocProp =
    EmblemProp[E, Assoc[Associatee]] forSome { type Associatee <: RootEntity }

  private type AssocSetProp =
    EmblemProp[E, Set[Assoc[Associatee]]] forSome { type Associatee <: RootEntity }

  private type AssocOptionProp =
    EmblemProp[E, Option[Assoc[Associatee]]] forSome { type Associatee <: RootEntity }

  private[longevity] val assocProps: Seq[AssocProp] = {
    emblem.props.flatMap { prop =>
      if (prop.typeKey.tag.tpe <:< typeOf[Assoc[_]])
        Some(prop).asInstanceOf[Option[AssocProp]]
      else
        None
    }
  }

  private[longevity] val assocSetProps: Seq[AssocSetProp] = {
    emblem.props.flatMap { prop =>
      def isAssocSetProp =
        prop.typeKey.tag.tpe <:< typeOf[Set[_]] &&
        prop.typeKey.tag.tpe.typeArgs.head <:< typeOf[Assoc[_]]
      if (isAssocSetProp)
        Some(prop).asInstanceOf[Option[AssocSetProp]]
      else
        None
    }
  }

  private[longevity] val assocOptionProps: Seq[AssocOptionProp] = {
    emblem.props.flatMap { prop =>
      if (prop.typeKey.tag.tpe <:< typeOf[Option[Assoc[_]]])
        Some(prop).asInstanceOf[Option[AssocOptionProp]]
      else
        None
    }
  }

}
