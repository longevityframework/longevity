package longevity.domain

import scala.collection.TraversableLike
import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds
import scala.reflect.runtime.universe._
import emblem._

object EntityType {

  // TODO scaladoc
  trait AssocLens[Associator <: Entity, Associatee <: Entity] {
    val associateeTypeTag: TypeTag[Associatee]
    def patchAssoc(associator: Associator, patcher: Assoc[Associatee] => Assoc[Associatee]): Associator
  }

  case class SingleAssocLens[Associator <: Entity, Associatee <: Entity : TypeTag](
    prop: EmblemProp[Associator, Assoc[Associatee]]
  ) extends AssocLens[Associator, Associatee] {
    val associateeTypeTag = typeTag[Associatee]
    def patchAssoc(associator: Associator, patcher: Assoc[Associatee] => Assoc[Associatee]) =
      prop.set(associator, patcher(prop.get(associator)))
  }

  case class AssocCollectionLens[
    Associator <: Entity,
    Associatee <: Entity : TypeTag,
    Collection[X] <: TraversableLike[X, Collection[X]]](
    getter: Associator => Collection[Assoc[Associatee]],
    setter: (Associator, Collection[Assoc[Associatee]]) => Associator
  )(
    implicit val cbf: CanBuildFrom[
      Collection[Assoc[Associatee]],
      Assoc[Associatee],
      Collection[Assoc[Associatee]]]
  ) extends AssocLens[Associator, Associatee] {
    implicit val associateeTypeTag = typeTag[Associatee]
    def patchAssoc(associator: Associator, patcher: Assoc[Associatee] => Assoc[Associatee]) =
      setter(associator, getter(associator) map patcher)
  }

  case class AssocOptionLens[Associator <: Entity, Associatee <: Entity : TypeTag](
    getter: Associator => Option[Assoc[Associatee]],
    setter: (Associator, Option[Assoc[Associatee]]) => Associator
  ) extends AssocLens[Associator, Associatee] {
    implicit val associateeTypeTag = typeTag[Associatee]
    def patchAssoc(associator: Associator, patcher: Assoc[Associatee] => Assoc[Associatee]) =
      setter(associator, getter(associator) map patcher)
  }

}

/** an entity type.
 * TODO scaladoc */
trait EntityType[E <: Entity] {

  def emblem: Emblem[E]

  /** override me!
   * TODO scaladoc */
  val assocLenses: List[EntityType.AssocLens[E, _ <: Entity]] = Nil

  private val assocProps: Seq[EmblemProp[E, Assoc[_]]] = {
    val x = emblem.props.flatMap { prop =>
      if (prop.typeTag.tpe <:< typeTag[Assoc[_]].tpe)
        Some(prop.asInstanceOf[EmblemProp[E, Assoc[_]]])
      else
        None

      // case prop: EmblemProp[E, Assoc[Entity]] =>
      //   prop
    }
    println(x)
    x
  }

  protected def lens1[Associatee <: Entity : TypeTag](
    prop: EmblemProp[E, Assoc[Associatee]]
  ) = {
    EntityType.SingleAssocLens(prop)
  }

  protected def lensN[Associatee <: Entity : TypeTag, Collection[X] <: TraversableLike[X, Collection[X]]](
    getter: E => Collection[Assoc[Associatee]]
  )(
    setter: (E, Collection[Assoc[Associatee]]) => E
  )(
    implicit cbf: CanBuildFrom[Collection[Assoc[Associatee]], Assoc[Associatee], Collection[Assoc[Associatee]]]
  ) = {
    EntityType.AssocCollectionLens(getter, setter)
  }

  protected def lensO[Associatee <: Entity : TypeTag](
    getter: E => Option[Assoc[Associatee]]
  )(
    setter: (E, Option[Assoc[Associatee]]) => E
  ) = {
    EntityType.AssocOptionLens(getter, setter)
  }

}
