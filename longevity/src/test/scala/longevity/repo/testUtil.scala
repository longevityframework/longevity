package longevity.repo

import emblem._
import longevity.domain._

object testUtil {

  case class User(name: String) extends Entity

  object UserType extends EntityType[User]

  case class Post(author: Assoc[User], content: String) extends Entity

  object PostType extends EntityType[Post]

  val entityTypes = EntityTypePool() + UserType + PostType

  val shorthands = ShorthandPool()

  val boundedContext = BoundedContext("blog", entityTypes, shorthands)

}
