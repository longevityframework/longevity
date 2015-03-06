package longevity.integration.master

import emblem._
import longevity.domain._

case class User(
  uri: String,
  firstName: String,
  lastName: String)
extends Entity

object UserType extends EntityType[User]
