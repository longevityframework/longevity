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
    def retrieve = repo.retrieve(this)
  }

  private var nextId = 0
  private var idToEMap = Map[Id[E], Persisted[E]]()
  private var originalCreations = Map[Unpersisted[E], Persisted[E]]()

  def create(unpersisted: Unpersisted[E]) = {
    val persisted = originalCreations.getOrElse(unpersisted, {
      val id = IntId(nextId)
      nextId += 1
      persist(id, handleAssocs(unpersisted.get))
    })
    originalCreations += (unpersisted -> persisted)
    persisted
  }

  // TODO name this stuff better
  protected def handleAssocs(e: E): E = {
    entityType.assocLenses.foldLeft(e) { (e, lens) =>
      persistAssoc(e, lens)
    }
  }

  private def persistAssoc[F <: Entity](e: E, lens: EntityType.AssocLens[E, F]): E = {
    implicit val ftag: TypeTag[F] = lens.ftag
    lens.patchAssoc(e, persistAssocPatcher)
  }

  private def persistAssocPatcher[F <: Entity](assoc: Assoc[F])(implicit ftag: TypeTag[F]): Assoc[F] = {
    assoc match {
      case AssocWithUnpersisted(u) =>
        val repo = repoPool.repoForEntityTypeTag(ftag)
        val persisted = repo.create(u)
        persisted.id
      case _ => assoc
    }
  }

  def retrieve(id: Id[E]) = idToEMap.getOrElse(id, NotFound(id))

  def update(persisted: Persisted[E]) =
    persist(persisted.id, handleAssocs(persisted.curr), persisted.currVersion)

  def delete(persisted: Persisted[E]) = {
    idToEMap -= persisted.id
    Deleted(persisted)
  }

  private def persist(id: Id[E], e: E): Persisted[E]= persist(id, e, 0L)

  private def persist(id: Id[E], e: E, version: Long) = {
      val persisted = Persisted[E](id, e, version)
      idToEMap += (id -> persisted)
      persisted
  }

}
