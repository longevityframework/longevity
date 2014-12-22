package longevity.domain

import scala.collection.TraversableLike
import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds
import scala.reflect.runtime.universe.TypeTag

object EntityType {

  // TODO scaladoc
  trait AssocLens[Associator <: Entity, Associatee <: Entity] {
    implicit val associateeTypeTag: TypeTag[Associatee]
    def patchAssoc(associator: Associator, patcher: Assoc[Associatee] => Assoc[Associatee]): Associator
  }

  case class SingleAssocLens[Associator <: Entity, Associatee <: Entity](
    getter: Associator => Assoc[Associatee],
    setter: (Associator, Assoc[Associatee]) => Associator
  )(
    implicit val associateeTypeTag: TypeTag[Associatee]
  ) extends AssocLens[Associator, Associatee] {
    def patchAssoc(associator: Associator, patcher: Assoc[Associatee] => Assoc[Associatee]) =
      setter(associator, patcher(getter(associator)))
  }

  case class AssocCollectionLens[
    Associator <: Entity,
    Associatee <: Entity,
    Collection[X] <: TraversableLike[X, Collection[X]]](
    getter: Associator => Collection[Assoc[Associatee]],
    setter: (Associator, Collection[Assoc[Associatee]]) => Associator
  )(
    implicit val associateeTypeTag: TypeTag[Associatee],
    implicit val cbf: CanBuildFrom[
      Collection[Assoc[Associatee]],
      Assoc[Associatee],
      Collection[Assoc[Associatee]]]
  ) extends AssocLens[Associator, Associatee] {
    def patchAssoc(associator: Associator, patcher: Assoc[Associatee] => Assoc[Associatee]) =
      setter(associator, getter(associator) map patcher)
  }

  case class AssocOptionLens[Associator <: Entity, Associatee <: Entity](
    getter: Associator => Option[Assoc[Associatee]],
    setter: (Associator, Option[Assoc[Associatee]]) => Associator
  )(
    implicit val associateeTypeTag: TypeTag[Associatee]
  ) extends AssocLens[Associator, Associatee] {
    def patchAssoc(associator: Associator, patcher: Assoc[Associatee] => Assoc[Associatee]) =
      setter(associator, getter(associator) map patcher)
  }

}

/** an entity type.
 * TODO scaladoc */
trait EntityType[E <: Entity] {

  /** override me!
   * TODO scaladoc */
  val assocLenses: List[EntityType.AssocLens[E, _ <: Entity]] = Nil

  protected def lens1[Associatee <: Entity](
    getter: E => Assoc[Associatee]
  )(
    setter: (E, Assoc[Associatee]) => E
  )(
    implicit associateeTypeTag: TypeTag[Associatee]
  ) = {
    EntityType.SingleAssocLens(getter, setter)
  }

  protected def lensN[Associatee <: Entity, Collection[X] <: TraversableLike[X, Collection[X]]](
    getter: E => Collection[Assoc[Associatee]]
  )(
    setter: (E, Collection[Assoc[Associatee]]) => E
  )(
    implicit associateeTypeTag: TypeTag[Associatee],
    cbf: CanBuildFrom[Collection[Assoc[Associatee]], Assoc[Associatee], Collection[Assoc[Associatee]]]
  ) = {
    EntityType.AssocCollectionLens(getter, setter)
  }

  protected def lensO[Associatee <: Entity](
    getter: E => Option[Assoc[Associatee]]
  )(
    setter: (E, Option[Assoc[Associatee]]) => E
  )(
    implicit associateeTypeTag: TypeTag[Associatee]
  ) = {
    EntityType.AssocOptionLens(getter, setter)
  }

}
