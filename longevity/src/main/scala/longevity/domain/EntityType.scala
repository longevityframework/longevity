package longevity.domain

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

  case class AssocSetLens[E <: Entity, Associate <: Entity](
    getter: E => Set[Assoc[Associate]],
    setter: (E, Set[Assoc[Associate]]) => E
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
  val assocLenses: List[EntityType.AssocLens[E, _ <: Entity]] = Nil

  protected def lens[Associate <: Entity](
    getter: E => Assoc[Associate]
  )(
    setter: (E, Assoc[Associate]) => E
  )(
    implicit associateTypeTag: TypeTag[Associate]
  ) = {
    EntityType.SingleAssocLens(getter, setter)
  }

  protected def lenss[Associate <: Entity](
    getter: E => Set[Assoc[Associate]]
  )(
    setter: (E, Set[Assoc[Associate]]) => E
  )(
    implicit associateTypeTag: TypeTag[Associate]
  ) = {
    EntityType.AssocSetLens(getter, setter)
  }

}
