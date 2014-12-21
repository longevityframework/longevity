package longevity.repo

import scala.reflect.runtime.universe.TypeTag

import longevity.domain._

object testUtils {

  class DummyRepo[E <: Entity](
    override val entityType: EntityType[E],
    override protected val repoPool: RepoPool
  )(
    implicit override val entityTypeTag: TypeTag[E]
  ) extends Repo[E] {
    def create(e: Unpersisted[E]) = ???
    def retrieve(id: Id[E]) = ???
    def update(p: Persisted[E]) = ???
    def delete(p: Persisted[E]) = ???
  }

  case class User(name: String) extends Entity

  object User extends EntityType[User]

  case class Post(author: Assoc[User], content: String) extends Entity

  object Post extends EntityType[Post]

}
