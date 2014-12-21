package longevity.domain

import scala.collection.TraversableLike
import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds
import scala.reflect.runtime.universe.TypeTag

object EntityType {

  trait AssocLens[E <: Entity, Associate <: Entity] {
    implicit val associateTypeTag: TypeTag[Associate]
    def patchAssoc(e: E, patcher: Assoc[Associate] => Assoc[Associate]): E
  }

  case class SingleAssocLens[E <: Entity, Associate <: Entity](
    getter: E => Assoc[Associate],
    setter: (E, Assoc[Associate]) => E
  )(
    implicit val associateTypeTag: TypeTag[Associate]
  ) extends AssocLens[E, Associate] {
    def patchAssoc(e: E, patcher: Assoc[Associate] => Assoc[Associate]) = setter(e, patcher(getter(e)))
  }

  case class AssocCollLens[E <: Entity, Associate <: Entity, I[X] <: TraversableLike[X,I[X]]](
    getter: E => I[Assoc[Associate]],
    setter: (E, I[Assoc[Associate]]) => E
  )(
    implicit val associateTypeTag: TypeTag[Associate],
    implicit val cbf: CanBuildFrom[I[Assoc[Associate]], Assoc[Associate], I[Assoc[Associate]]]
  ) extends AssocLens[E, Associate] {
    def patchAssoc(e: E, patcher: Assoc[Associate] => Assoc[Associate]) =
      setter(e, getter(e) map patcher)
  }

  case class AssocOptLens[E <: Entity, Associate <: Entity](
    getter: E => Option[Assoc[Associate]],
    setter: (E, Option[Assoc[Associate]]) => E
  )(
    implicit val associateTypeTag: TypeTag[Associate]
  ) extends AssocLens[E, Associate] {
    def patchAssoc(e: E, patcher: Assoc[Associate] => Assoc[Associate]) =
      setter(e, getter(e) map patcher)
  }

}

/** an entity type */
trait EntityType[E <: Entity] {

  // override me!
  /** TODO scaladoc */
  val assocLenses: List[EntityType.AssocLens[E, _ <: Entity]] = Nil

  protected def lens1[Associate <: Entity](
    getter: E => Assoc[Associate]
  )(
    setter: (E, Assoc[Associate]) => E
  )(
    implicit associateTypeTag: TypeTag[Associate]
  ) = {
    EntityType.SingleAssocLens(getter, setter)
  }

  protected def lensN[Associate <: Entity, I[X] <: TraversableLike[X,I[X]]](
    getter: E => I[Assoc[Associate]]
  )(
    setter: (E, I[Assoc[Associate]]) => E
  )(
    implicit associateTypeTag: TypeTag[Associate],
    cbf: CanBuildFrom[I[Assoc[Associate]], Assoc[Associate], I[Assoc[Associate]]]
  ) = {
    EntityType.AssocCollLens(getter, setter)
  }

  protected def lensO[Associate <: Entity](
    getter: E => Option[Assoc[Associate]]
  )(
    setter: (E, Option[Assoc[Associate]]) => E
  )(
    implicit associateTypeTag: TypeTag[Associate]
  ) = {
    EntityType.AssocOptLens(getter, setter)
  }

}
