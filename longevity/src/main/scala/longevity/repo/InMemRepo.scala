package longevity.repo

import scala.reflect.runtime.universe.TypeTag

import longevity.domain._

abstract class InMemRepo[E <: Entity](
  override val entityType: EntityType[E]
)(
  implicit override val entityTypeTag: TypeTag[E]
) extends Repo[E] {
  repo =>

  case class IntId(i: Int) extends Id[E] {
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this)
    def unpersisted = throw new Assoc.AssocIsPersistedException(this)
  }

  private var nextId = 0
  private var idToEntityMap = Map[Id[E], Persisted[E]]()

  // TODO: can we explain why this is falling out of date with regards to updates?
  private var originalCreations = Map[Unpersisted[E], Persisted[E]]()

  def create(unpersisted: Unpersisted[E]) = {
    val persisted = originalCreations.getOrElse(unpersisted, {
      val id = IntId(nextId)
      nextId += 1
      persist(id, patchUnpersistedAssocs(unpersisted.get))
    })
    originalCreations += (unpersisted -> persisted)
    persisted
  }

  def retrieve(id: Id[E]) = idToEntityMap.getOrElse(id, NotFound(id))

  def update(persisted: Persisted[E]) =
    persist(persisted.id, patchUnpersistedAssocs(persisted.curr), persisted.currVersion)

  def delete(persisted: Persisted[E]) = {
    idToEntityMap -= persisted.id
    Deleted(persisted)
  }

  private def patchUnpersistedAssocs(e: E): E = {
    entityType.assocLenses.foldLeft(e) { (e, lens) =>
      patchUnpersistedAssoc(e, lens)
    }
  }

  private def patchUnpersistedAssoc[F <: Entity](e: E, lens: EntityType.AssocLens[E, F]): E = {
    implicit val associateeTypeTag: TypeTag[F] = lens.associateeTypeTag
    lens.patchAssoc(e, persistAssocWhenUnpersisted)
  }

  private def persistAssocWhenUnpersisted[Associatee <: Entity](assoc: Assoc[Associatee])(
    implicit associateeTypeTag: TypeTag[Associatee]
  ): Assoc[Associatee] = {
    assoc match {
      case UnpersistedAssoc(u) =>
        val repo = repoPool.repoForEntityTypeTag(associateeTypeTag)
        val persisted = repo.create(u)
        persisted.id
      case _ => assoc
    }
  }

  private def persist(id: Id[E], e: E): Persisted[E]= persist(id, e, 0L)

  private def persist(id: Id[E], e: E, version: Long) = {
      val persisted = Persisted[E](id, e, version)
      idToEntityMap += (id -> persisted)
      persisted
  }

}
