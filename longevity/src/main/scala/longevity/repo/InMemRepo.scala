package longevity.repo

import scala.reflect.ClassTag

import longevity.domain._

abstract class InMemRepo[E <: Entity](
  override val entityType: EntityType[E]
)(
  implicit override val entityClassTag: ClassTag[E]
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

  // TODO: generify this
  // override me!
  //protected def handleAssocs(e: E): E = e
  protected def handleAssocs(e: E): E = {
    // val ruType = entityClassTag.tpe
    // println(s"entityClassTag $entityClassTag")
    // println(s"ruType $ruType")
    e
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
