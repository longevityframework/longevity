package longevity.domain

import scala.reflect.runtime.universe.TypeTag

object EntityType {

  trait AssocLens[E <: Entity, F <: Entity] {
    val getter: E => Assoc[F]
    val setter: (E, Assoc[F]) => E
    implicit val ftag: TypeTag[F]
    def patchAssoc(e: E, patcher: Assoc[F] => Assoc[F]): E
  }

  case class SingleAssocLens[E <: Entity, F <: Entity](
    getter: E => Assoc[F],
    setter: (E, Assoc[F]) => E
  )(
    implicit val ftag: TypeTag[F]
  ) extends AssocLens[E, F] {
    def patchAssoc(e: E, patcher: Assoc[F] => Assoc[F]) = setter(e, patcher(getter(e)))
  }

}

/** an entity type */
trait EntityType[E <: Entity] {

  // override me!
  val assocLenses: List[EntityType.AssocLens[E, _ <: Entity]] = Nil

  protected def lens[F <: Entity](
    getter: E => Assoc[F])(
    setter: (E, Assoc[F]) => E)(
    implicit ftag: TypeTag[F]) =
    EntityType.SingleAssocLens(getter, setter)

}
