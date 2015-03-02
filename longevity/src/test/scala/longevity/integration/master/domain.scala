package longevity.integration.master

import emblem._
import longevity.domain._

object domain {

  case class User(
    uri: String,
    firstName: String,
    lastName: String)
  extends Entity

  object UserType extends EntityType[User]

  val entityTypes = EntityTypePool() + UserType

  val boundedContext = BoundedContext(entityTypes, ShorthandPool())

}
