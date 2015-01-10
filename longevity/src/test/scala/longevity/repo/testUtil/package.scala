package longevity.repo

import scala.reflect.runtime.universe.TypeTag

import emblem._
import longevity.domain._

object testUtil {

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

  object User extends EntityType[User] {

    lazy val emblem = new Emblem[User](
      "longevity.repo.testUtil",
      "User",
      Seq(
        new EmblemProp[User, String]("name", _.name, (p, name) => p.copy(name = name))
      ),
      User(null)
    )

  }

  case class Post(author: Assoc[User], content: String) extends Entity

  object Post extends EntityType[Post] {

    lazy val emblem = new Emblem[Post](
      "longevity.repo.testUtil",
      "Post",
      Seq(
        new EmblemProp[Post, Assoc[User]]("author", _.author, (p, author) => p.copy(author = author)),
        new EmblemProp[Post, String]("content", _.content, (p, content) => p.copy(content = content))
      ),
      Post(null, null)
    )
  }

}
